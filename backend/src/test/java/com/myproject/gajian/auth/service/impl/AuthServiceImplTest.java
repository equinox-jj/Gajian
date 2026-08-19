package com.myproject.gajian.auth.service.impl;

import com.myproject.gajian.auth.dto.ChangePasswordRequest;
import com.myproject.gajian.auth.dto.LoginRequest;
import com.myproject.gajian.auth.dto.LoginResponse;
import com.myproject.gajian.auth.dto.RefreshRequest;
import com.myproject.gajian.auth.entity.AppUser;
import com.myproject.gajian.auth.entity.Role;
import com.myproject.gajian.auth.repository.AppUserRepository;
import com.myproject.gajian.auth.service.AuthService;
import com.myproject.gajian.auth.service.RefreshTokenService;
import com.myproject.gajian.auth.service.RotatedToken;
import com.myproject.gajian.common.exception.ApiException;
import com.myproject.gajian.common.exception.ErrorCode;
import com.myproject.gajian.security.AccessToken;
import com.myproject.gajian.security.AppUserDetails;
import com.myproject.gajian.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;
    private AppUser user;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                authenticationManager, jwtTokenProvider, refreshTokenService, appUserRepository, passwordEncoder);
        user = AppUser.builder()
                .id(UUID.randomUUID())
                .email("joshua@example.com")
                .password("encoded-old")
                .role(Role.MANAGER)
                .active(true)
                .build();
    }

    @Test
    void loginReturnsAccessAndRefreshTokensForAuthenticatedUser() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(new AppUserDetails(user), null);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(new AccessToken("jwt-value", 900L));
        when(refreshTokenService.issue(user)).thenReturn("raw-refresh-token");

        // Act
        LoginResponse response = authService.login(new LoginRequest("joshua@example.com", "secret"));

        // Assert
        assertThat(response).isEqualTo(new LoginResponse("jwt-value", "raw-refresh-token", 900L, Role.MANAGER));
    }

    @Test
    void refreshIssuesNewTokenPairFromRotatedRefreshToken() {
        // Arrange
        when(refreshTokenService.rotate("old-refresh")).thenReturn(new RotatedToken(user, "new-refresh"));
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(new AccessToken("new-jwt", 900L));

        // Act
        LoginResponse response = authService.refresh(new RefreshRequest("old-refresh"));

        // Assert
        assertThat(response).isEqualTo(new LoginResponse("new-jwt", "new-refresh", 900L, Role.MANAGER));
    }

    @Test
    void changePasswordRevokesEverySessionAfterStoringNewPassword() {
        // Arrange
        when(appUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current", "encoded-old")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new");

        // Act
        authService.changePassword(user.getId(), new ChangePasswordRequest("current", "new-password"));

        // Assert
        assertThat(user.getPassword()).isEqualTo("encoded-new");
        verify(refreshTokenService).revokeAllForUser(user.getId());
    }

    @Test
    void changePasswordRejectsWrongCurrentPassword() {
        // Arrange
        when(appUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-old")).thenReturn(false);

        // Act
        Throwable thrown = catchThrowable(
                () -> authService.changePassword(user.getId(), new ChangePasswordRequest("wrong", "new-password")));

        // Assert
        assertThat(thrown).isInstanceOf(ApiException.class)
                .extracting(exception -> ((ApiException) exception).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_CREDENTIALS);
        assertThat(user.getPassword()).isEqualTo("encoded-old");
        verify(refreshTokenService, never()).revokeAllForUser(any());
    }
}
