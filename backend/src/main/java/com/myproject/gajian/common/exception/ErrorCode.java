package com.myproject.gajian.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Email atau password salah"),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "Akun tidak aktif"),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Autentikasi diperlukan"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Akses ditolak"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh token tidak dikenali"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh token expired"),
    SESSION_REVOKED(HttpStatus.UNAUTHORIZED, "Sesi tidak valid — semua sesi telah di-logout"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User tidak ditemukan"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Terjadi kesalahan pada server");

    private final HttpStatus status;
    private final String defaultMessage;
}
