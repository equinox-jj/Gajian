package com.myproject.gajian.auth.controller;

import com.myproject.gajian.auth.dto.LoginResponse;
import com.myproject.gajian.auth.entity.Role;
import com.myproject.gajian.auth.service.AuthService;
import com.myproject.gajian.config.SecurityConfig;
import com.myproject.gajian.security.SecurityErrorHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, SecurityErrorHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void loginReturnsTokensWrappedInSuccessEnvelope() throws Exception {
        // Arrange
        when(authService.login(any())).thenReturn(new LoginResponse("jwt-value", "refresh-value", 900L, Role.ADMIN));

        // Act
        var result = mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"joshua@example.com\",\"password\":\"secret\"}"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login berhasil"))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-value"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-value"))
                .andExpect(jsonPath("$.data.expiresIn").value(900))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.meta").doesNotExist());
    }

    @Test
    void loginReturnsValidationErrorArrayForMalformedRequest() throws Exception {
        // Arrange

        // Act
        var result = mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"not-an-email\",\"password\":\"\"}"));

        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void unreadableRequestBodyIsReportedAsBadRequestNotServerError() throws Exception {
        // Arrange

        // Act
        var result = mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": "));

        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error_code").value("MALFORMED_REQUEST"));
    }

    @Test
    void wrongHttpMethodIsReportedAsMethodNotAllowedNotServerError() throws Exception {
        // Arrange

        // Act
        var result = mockMvc.perform(get("/v1/auth/login"));

        // Assert
        result.andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error_code").value("METHOD_NOT_ALLOWED"));
    }

    @Test
    void unsupportedContentTypeIsReportedAsUnsupportedMediaTypeNotServerError() throws Exception {
        // Arrange

        // Act
        var result = mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.TEXT_PLAIN)
                .content("email=joshua@example.com"));

        // Assert
        result.andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error_code").value("UNSUPPORTED_MEDIA_TYPE"));
    }

    @Test
    void unauthenticatedLogoutIsRejectedThroughTheErrorEnvelope() throws Exception {
        // Arrange

        // Act
        var result = mockMvc.perform(post("/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"refresh-value\"}"));

        // Assert
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error_code").value("UNAUTHENTICATED"));
    }

    @Test
    void securityErrorsAreWrittenAsUtf8() throws Exception {
        // Arrange

        // Act
        var result = mockMvc.perform(post("/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"refresh-value\"}"));

        // Assert
        result.andExpect(status().isUnauthorized())
                .andExpect(content().encoding(StandardCharsets.UTF_8));
    }

    @Test
    void logoutRevokesTheRefreshTokenOfTheAuthenticatedUser() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        var result = mockMvc.perform(post("/v1/auth/logout")
                .with(jwt().jwt(token -> token.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"refresh-value\"}"));

        // Assert
        result.andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(authService).logout(eq(userId), any());
    }

    @Test
    void logoutAllReturnsNoContent() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        var result = mockMvc.perform(post("/v1/auth/logout-all")
                .with(jwt().jwt(token -> token.subject(userId.toString()))));

        // Assert
        result.andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(authService).logoutAll(userId);
    }

    @Test
    void changePasswordReturnsNoContent() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        var result = mockMvc.perform(post("/v1/auth/change-password")
                .with(jwt().jwt(token -> token.subject(userId.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"oldPassword\":\"current-one\",\"newPassword\":\"new-password\"}"));

        // Assert
        result.andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(authService).changePassword(eq(userId), any());
    }
}
