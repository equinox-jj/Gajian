package com.myproject.gajian.auth.service.impl;

import com.myproject.gajian.auth.dto.ChangePasswordRequest;
import com.myproject.gajian.auth.dto.LoginRequest;
import com.myproject.gajian.auth.dto.LoginResponse;
import com.myproject.gajian.auth.dto.RefreshRequest;
import com.myproject.gajian.auth.entity.AppUser;
import com.myproject.gajian.auth.repository.AppUserRepository;
import com.myproject.gajian.auth.service.AuthService;
import com.myproject.gajian.auth.service.RefreshTokenService;
import com.myproject.gajian.auth.service.RotatedToken;
import com.myproject.gajian.common.exception.ApiException;
import com.myproject.gajian.common.exception.ErrorCode;
import com.myproject.gajian.security.AccessToken;
import com.myproject.gajian.security.AppUserDetails;
import com.myproject.gajian.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        AppUser user = ((AppUserDetails) authentication.getPrincipal()).user();

        return buildLoginResponse(user, refreshTokenService.issue(user));
    }

    @Override
    @Transactional
    public LoginResponse refresh(RefreshRequest request) {
        RotatedToken rotated = refreshTokenService.rotate(request.refreshToken());
        return buildLoginResponse(rotated.user(), rotated.rawToken());
    }

    @Override
    @Transactional
    public void logout(UUID userId, RefreshRequest request) {
        refreshTokenService.revoke(userId, request.refreshToken());
    }

    @Override
    @Transactional
    public void logoutAll(UUID userId) {
        refreshTokenService.revokeAllForUser(userId);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        refreshTokenService.revokeAllForUser(userId);
    }

    private LoginResponse buildLoginResponse(AppUser user, String refreshToken) {
        AccessToken accessToken = jwtTokenProvider.generateAccessToken(user);
        return new LoginResponse(
                accessToken.value(), refreshToken, accessToken.expiresInSeconds(), user.getRole());
    }
}
