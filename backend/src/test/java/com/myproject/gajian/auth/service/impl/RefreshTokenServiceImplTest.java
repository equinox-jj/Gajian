package com.myproject.gajian.auth.service.impl;

import com.myproject.gajian.auth.entity.AppUser;
import com.myproject.gajian.auth.entity.RefreshToken;
import com.myproject.gajian.auth.entity.Role;
import com.myproject.gajian.auth.repository.RefreshTokenRepository;
import com.myproject.gajian.auth.service.RefreshTokenService;
import com.myproject.gajian.auth.service.RotatedToken;
import com.myproject.gajian.common.exception.ApiException;
import com.myproject.gajian.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private RefreshTokenRevoker refreshTokenRevoker;

    private RefreshTokenService refreshTokenService;
    private AppUser user;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository, refreshTokenRevoker);
        user = AppUser.builder()
                .id(UUID.randomUUID())
                .email("joshua@example.com")
                .password("encoded")
                .role(Role.EMPLOYEE)
                .active(true)
                .build();
    }

    @Test
    void issueStoresHashedTokenAndReturnsRawToken() {
        // Arrange
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(call -> call.getArgument(0));

        // Act
        String rawToken = refreshTokenService.issue(user);

        // Assert
        ArgumentCaptor<RefreshToken> saved = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(saved.capture());
        assertThat(saved.getValue().getTokenHash()).isNotEqualTo(rawToken).hasSize(64);
        assertThat(saved.getValue().getUser()).isEqualTo(user);
        assertThat(saved.getValue().getRevokedAt()).isNull();
    }

    @Test
    void rotateRevokesPresentedTokenAndIssuesReplacement() {
        // Arrange
        String rawToken = issuedRawToken();
        RefreshToken existing = activeToken(rawToken);
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(existing));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(call -> call.getArgument(0));

        // Act
        RotatedToken rotated = refreshTokenService.rotate(rawToken);

        // Assert
        assertThat(rotated.user()).isEqualTo(user);
        assertThat(rotated.rawToken()).isNotEqualTo(rawToken);
        assertThat(existing.getRevokedAt()).isNotNull();
        assertThat(existing.getReplacedByToken()).isNotNull();
    }

    @Test
    void rotateRevokesEverySessionWhenRevokedTokenIsReused() {
        // Arrange
        String rawToken = issuedRawToken();
        RefreshToken revoked = activeToken(rawToken);
        revoked.setRevokedAt(Instant.now().minus(1, ChronoUnit.MINUTES));
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(revoked));

        // Act
        Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> refreshTokenService.rotate(rawToken));

        // Assert
        assertThat(thrown).isInstanceOf(ApiException.class)
                .extracting(exception -> ((ApiException) exception).getErrorCode())
                .isEqualTo(ErrorCode.SESSION_REVOKED);
        verify(refreshTokenRevoker).revokeAllForUser(user.getId());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void rotateRejectsExpiredToken() {
        // Arrange
        String rawToken = issuedRawToken();
        RefreshToken expired = activeToken(rawToken);
        expired.setExpiresAt(Instant.now().minus(1, ChronoUnit.MINUTES));
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(expired));

        // Act
        Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> refreshTokenService.rotate(rawToken));

        // Assert
        assertThat(thrown).isInstanceOf(ApiException.class)
                .extracting(exception -> ((ApiException) exception).getErrorCode())
                .isEqualTo(ErrorCode.REFRESH_TOKEN_EXPIRED);
    }

    @Test
    void rotateRejectsUnknownToken() {
        // Arrange
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        // Act
        Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> refreshTokenService.rotate("bogus"));

        // Assert
        assertThat(thrown).isInstanceOf(ApiException.class)
                .extracting(exception -> ((ApiException) exception).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @Test
    void revokeIgnoresTokenBelongingToAnotherUser() {
        // Arrange
        String rawToken = issuedRawToken();
        RefreshToken othersToken = activeToken(rawToken);
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(othersToken));

        // Act
        refreshTokenService.revoke(UUID.randomUUID(), rawToken);

        // Assert
        assertThat(othersToken.getRevokedAt()).isNull();
    }

    @Test
    void revokeMarksOwnTokenRevoked() {
        // Arrange
        String rawToken = issuedRawToken();
        RefreshToken ownToken = activeToken(rawToken);
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(ownToken));

        // Act
        refreshTokenService.revoke(user.getId(), rawToken);

        // Assert
        assertThat(ownToken.getRevokedAt()).isNotNull();
    }

    private String issuedRawToken() {
        return "raw-token-value";
    }

    private RefreshToken activeToken(String rawToken) {
        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(user)
                .tokenHash(rawToken)
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();
    }
}
