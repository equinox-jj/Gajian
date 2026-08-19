package com.myproject.gajian.common.constant;

import java.time.Duration;

public final class AuthConstants {

    public static final String BASE_PATH = ApiConstants.API_V1 + "/auth";

    public static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);
    public static final int REFRESH_TOKEN_BYTES = 64;
    public static final String REFRESH_TOKEN_PURGE_CRON = "0 0 3 * * *";

    public static final String MESSAGE_LOGIN_SUCCESS = "Login berhasil";
    public static final String MESSAGE_TOKEN_REFRESHED = "Token berhasil diperbarui";

    private AuthConstants() {
    }
}
