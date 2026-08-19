package com.myproject.gajian.auth;

import com.jayway.jsonpath.JsonPath;
import com.myproject.gajian.auth.entity.AppUser;
import com.myproject.gajian.auth.entity.Role;
import com.myproject.gajian.auth.repository.AppUserRepository;
import com.myproject.gajian.auth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    private static final String EMAIL = "flow@example.com";
    private static final String PASSWORD = "correct-horse";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        appUserRepository.deleteAll();
        appUserRepository.save(AppUser.builder()
                .email(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .role(Role.ADMIN)
                .active(true)
                .build());
    }

    @Test
    void loginRejectsWrongPassword() throws Exception {
        // Arrange

        // Act
        var result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody(EMAIL, "wrong-password")));

        // Assert
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void refreshRotatesTheStoredTokenAndKeepsExactlyOneUsableSession() throws Exception {
        // Arrange
        String firstRefreshToken = login();

        // Act
        String secondRefreshToken = refresh(firstRefreshToken);

        // Assert
        assertThat(secondRefreshToken).isNotEqualTo(firstRefreshToken);
        assertThat(refreshTokenRepository.findAll())
                .hasSize(2)
                .filteredOn(token -> token.getRevokedAt() == null)
                .hasSize(1);
    }

    @Test
    void reusingARotatedRefreshTokenRevokesEverySession() throws Exception {
        // Arrange
        String firstRefreshToken = login();
        refresh(firstRefreshToken);

        // Act
        var result = mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + firstRefreshToken + "\"}"));

        // Assert
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("SESSION_REVOKED"));
        assertThat(refreshTokenRepository.findAll()).allMatch(token -> token.getRevokedAt() != null);
    }

    private String login() throws Exception {
        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.data.refreshToken");
    }

    private String refresh(String refreshToken) throws Exception {
        String body = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.data.refreshToken");
    }

    private String loginBody(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }
}
