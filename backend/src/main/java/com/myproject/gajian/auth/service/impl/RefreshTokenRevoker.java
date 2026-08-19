package com.myproject.gajian.auth.service.impl;

import com.myproject.gajian.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Revocation that must outlive the caller's transaction. Reuse detection rejects the request it is
 * serving, so revoking inline would be rolled back by the very exception that reports the theft —
 * and it lives in its own bean because {@code REQUIRES_NEW} is ignored on a self-invoked method.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenRevoker {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeAllForUser(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId, Instant.now());
    }
}
