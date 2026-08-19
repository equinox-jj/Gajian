package com.myproject.gajian.common.constant;

public final class ApiConstants {

    // Carried in the controller mappings rather than server.servlet.context-path so MockMvc exercises
    // the same URLs clients call; context-path is invisible to MockMvc.
    public static final String API_V1 = "/v1";

    private ApiConstants() {
    }
}
