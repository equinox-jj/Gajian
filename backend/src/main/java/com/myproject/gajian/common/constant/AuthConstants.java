package com.myproject.gajian.common.constant;

import java.time.Duration;

public final class AuthConstants {

    public static final String BASE_PATH = "/auth";

    public static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);
    public static final int REFRESH_TOKEN_BYTES = 64;

    public static final String MESSAGE_LOGIN_SUCCESS = "Login berhasil";
    public static final String MESSAGE_TOKEN_REFRESHED = "Token berhasil diperbarui";
    public static final String MESSAGE_LOGOUT_SUCCESS = "Logout berhasil";
    public static final String MESSAGE_LOGOUT_ALL_SUCCESS = "Semua sesi berhasil dikeluarkan";
    public static final String MESSAGE_PASSWORD_CHANGED = "Password berhasil diubah";

    private AuthConstants() {
    }
}
