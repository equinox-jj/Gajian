package com.myproject.gajian.auth.dto;

import com.myproject.gajian.auth.entity.Role;

public record LoginResponse(String accessToken, String refreshToken, long expiresIn, Role role) {
}
