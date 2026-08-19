package com.myproject.gajian.security;

public record AccessToken(String value, long expiresInSeconds) {
}
