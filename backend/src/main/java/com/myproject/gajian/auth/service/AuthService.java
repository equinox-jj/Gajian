package com.myproject.gajian.auth.service;

import com.myproject.gajian.auth.dto.ChangePasswordRequest;
import com.myproject.gajian.auth.dto.LoginRequest;
import com.myproject.gajian.auth.dto.LoginResponse;
import com.myproject.gajian.auth.dto.RefreshRequest;

import java.util.UUID;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(RefreshRequest request);

    void logout(UUID userId, RefreshRequest request);

    void logoutAll(UUID userId);

    void changePassword(UUID userId, ChangePasswordRequest request);
}
