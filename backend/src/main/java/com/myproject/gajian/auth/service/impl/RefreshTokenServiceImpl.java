package com.myproject.gajian.auth.service.impl;

import com.myproject.gajian.auth.entity.AppUser;
import com.myproject.gajian.auth.entity.RefreshToken;
import com.myproject.gajian.auth.repository.RefreshTokenRepository;
import com.myproject.gajian.auth.service.RefreshTokenService;
import com.myproject.gajian.auth.service.RotatedToken;
import com.myproject.gajian.common.constant.AuthConstants;
import com.myproject.gajian.common.exception.ApiException;
import com.myproject.gajian.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final String HASH_ALGORITHM = "SHA-256";

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenRevoker refreshTokenRevoker;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public String issue(AppUser user) {
        String rawToken = generateRawToken();
        persist(user, rawToken);
        return rawToken;
    }

    @Override
    @Transactional
    public RotatedToken rotate(String rawToken) {
        RefreshToken presented = refreshTokenRepository.findByTokenHashForUpdate(hash(rawToken))
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (presented.getRevokedAt() != null) {
            // A revoked token resurfacing means it was captured: kill every session, not just this request.
            log.warn("Refresh token reuse detected for user {}", presented.getUser().getId());
            refreshTokenRevoker.revokeAllForUser(presented.getUser().getId());
            throw new ApiException(ErrorCode.SESSION_REVOKED);
        }
        if (presented.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        AppUser user = presented.getUser();
        // /auth/refresh is public, so this is the only place the disabled check runs for a returning
        // session — DaoAuthenticationProvider only sees the login path.
        if (!user.isActive()) {
            throw new ApiException(ErrorCode.ACCOUNT_DISABLED);
        }

        String replacementRawToken = generateRawToken();
        presented.setReplacedByToken(persist(user, replacementRawToken));
        presented.setRevokedAt(Instant.now());

        return new RotatedToken(user, replacementRawToken);
    }

    @Override
    @Transactional
    public void revoke(UUID userId, String rawToken) {
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .filter(candidate -> candidate.getUser().getId().equals(userId))
                .filter(candidate -> candidate.getRevokedAt() == null)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_REFRESH_TOKEN));

        token.setRevokedAt(Instant.now());
    }

    @Override
    @Transactional
    public void revokeAllForUser(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId, Instant.now());
    }

    @Override
    @Transactional
    @Scheduled(cron = AuthConstants.REFRESH_TOKEN_PURGE_CRON)
    public void purgeExpiredTokens() {
        Instant now = Instant.now();
        refreshTokenRepository.clearReplacementLinksOfExpiredAndRevoked(now);
        int purged = refreshTokenRepository.deleteExpiredAndRevoked(now);
        log.info("Purged {} expired refresh tokens", purged);
    }

    private RefreshToken persist(AppUser user, String rawToken) {
        return refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(hash(rawToken))
                .expiresAt(Instant.now().plus(AuthConstants.REFRESH_TOKEN_TTL))
                .build());
    }

    private String generateRawToken() {
        byte[] token = new byte[AuthConstants.REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    private String hash(String rawToken) {
        try {
            // SHA-256, not BCrypt: the token is already high-entropy, so a slow KDF only costs latency.
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(HASH_ALGORITHM + " is required but unavailable", exception);
        }
    }
}
