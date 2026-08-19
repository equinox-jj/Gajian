package com.myproject.gajian.common.constant;

public final class SecurityConstants {

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String[] PUBLIC_ENDPOINTS = {
            AuthConstants.BASE_PATH + "/login",
            AuthConstants.BASE_PATH + "/refresh"
    };

    private SecurityConstants() {
    }
}
