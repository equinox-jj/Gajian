package com.myproject.gajian.auth.service;

import com.myproject.gajian.auth.entity.AppUser;

import java.util.UUID;

public interface RefreshTokenService {

    String issue(AppUser user);

    RotatedToken rotate(String rawToken);

    void revoke(UUID userId, String rawToken);

    void revokeAllForUser(UUID userId);

    void purgeExpiredTokens();
}
