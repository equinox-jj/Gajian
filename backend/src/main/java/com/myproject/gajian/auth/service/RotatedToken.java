package com.myproject.gajian.auth.service;

import com.myproject.gajian.auth.entity.AppUser;

public record RotatedToken(AppUser user, String rawToken) {
}
