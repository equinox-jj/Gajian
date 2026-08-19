package com.myproject.gajian.auth;

import com.jayway.jsonpath.JsonPath;
import com.myproject.gajian.auth.entity.AppUser;
import com.myproject.gajian.auth.entity.RefreshToken;
import com.myproject.gajian.auth.entity.Role;
import com.myproject.gajian.auth.repository.AppUserRepository;
import com.myproject.gajian.auth.repository.RefreshTokenRepository;
import com.myproject.gajian.auth.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
    private RefreshTokenService refreshTokenService;
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
        var result = mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody(EMAIL, "wrong-password")));

        // Assert
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void loginOfADeactivatedAccountIsIndistinguishableFromAWrongPassword() throws Exception {
        // Arrange
        deactivateUser();

        // Act
        var result = mockMvc.perform(post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody(EMAIL, PASSWORD)));

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
    void refreshIsRejectedOnceTheAccountIsDeactivated() throws Exception {
        // Arrange
        String refreshToken = login();
        deactivateUser();

        // Act
        var result = mockMvc.perform(post("/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshBody(refreshToken)));

        // Assert
        result.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error_code").value("ACCOUNT_DISABLED"));
        assertThat(refreshTokenRepository.findAll()).hasSize(1);
    }

    @Test
    void reusingARotatedRefreshTokenRevokesEverySession() throws Exception {
        // Arrange
        String firstRefreshToken = login();
        refresh(firstRefreshToken);

        // Act
        var result = mockMvc.perform(post("/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshBody(firstRefreshToken)));

        // Assert
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("SESSION_REVOKED"));
        assertThat(refreshTokenRepository.findAll()).allMatch(token -> token.getRevokedAt() != null);
    }

    @Test
    void logoutRevokesThePresentedTokenAndReturnsNoContent() throws Exception {
        // Arrange
        String session = loginResponseBody();

        // Act
        var result = mockMvc.perform(post("/v1/auth/logout")
                .header("Authorization", "Bearer " + accessTokenOf(session))
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshBody(refreshTokenOf(session))));

        // Assert
        result.andExpect(status().isNoContent());
        assertThat(refreshTokenRepository.findAll()).allMatch(token -> token.getRevokedAt() != null);
    }

    @Test
    void logoutReportsFailureWhenTheTokenIsNotOurs() throws Exception {
        // Arrange
        String session = loginResponseBody();

        // Act
        var result = mockMvc.perform(post("/v1/auth/logout")
                .header("Authorization", "Bearer " + accessTokenOf(session))
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshBody("a-token-we-never-issued")));

        // Assert
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("INVALID_REFRESH_TOKEN"));
        assertThat(refreshTokenRepository.findAll()).allMatch(token -> token.getRevokedAt() == null);
    }

    @Test
    void unknownPathIsReportedAsNotFoundNotServerError() throws Exception {
        // Arrange
        String accessToken = accessTokenOf(loginResponseBody());

        // Act
        var result = mockMvc.perform(post("/v1/auth/does-not-exist")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

        // Assert
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("NOT_FOUND"));
    }

    @Test
    void unknownPathIsNotDisclosedToAnAnonymousCaller() throws Exception {
        // Arrange

        // Act
        var result = mockMvc.perform(post("/v1/auth/does-not-exist")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

        // Assert
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("UNAUTHENTICATED"));
    }

    @Test
    void purgeRemovesExpiredRevokedTokensButKeepsTheEvidenceOfLiveOnes() {
        // Arrange
        AppUser user = appUserRepository.findByEmail(EMAIL).orElseThrow();
        Instant past = Instant.now().minus(1, ChronoUnit.DAYS);
        RefreshToken expiredAndRevoked = refreshTokenRepository.save(RefreshToken.builder()
                .user(user).tokenHash("expired-revoked").expiresAt(past).revokedAt(past).build());
        RefreshToken expiredOnly = refreshTokenRepository.save(RefreshToken.builder()
                .user(user).tokenHash("expired-only").expiresAt(past).build());
        RefreshToken revokedOnly = refreshTokenRepository.save(RefreshToken.builder()
                .user(user).tokenHash("revoked-only")
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS)).revokedAt(past).build());

        // Act
        refreshTokenService.purgeExpiredTokens();

        // Assert
        assertThat(refreshTokenRepository.findAll())
                .extracting(RefreshToken::getId)
                .doesNotContain(expiredAndRevoked.getId())
                .contains(expiredOnly.getId(), revokedOnly.getId());
    }

    @Test
    void purgeDropsAWholeRotationChainWithoutTrippingTheReplacedByForeignKey() throws Exception {
        // Arrange
        String firstRefreshToken = login();
        refresh(firstRefreshToken);
        Instant past = Instant.now().minus(1, ChronoUnit.DAYS);
        refreshTokenRepository.saveAll(refreshTokenRepository.findAll().stream()
                .peek(token -> {
                    token.setExpiresAt(past);
                    token.setRevokedAt(past);
                })
                .toList());

        // Act
        refreshTokenService.purgeExpiredTokens();

        // Assert
        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }

    private void deactivateUser() {
        AppUser user = appUserRepository.findByEmail(EMAIL).orElseThrow();
        user.setActive(false);
        appUserRepository.save(user);
    }

    private String accessTokenOf(String loginResponseBody) {
        return JsonPath.read(loginResponseBody, "$.data.accessToken");
    }

    private String refreshTokenOf(String loginResponseBody) {
        return JsonPath.read(loginResponseBody, "$.data.refreshToken");
    }

    private String login() throws Exception {
        return refreshTokenOf(loginResponseBody());
    }

    private String loginResponseBody() throws Exception {
        return mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String refresh(String refreshToken) throws Exception {
        String body = mockMvc.perform(post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshBody(refreshToken)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.data.refreshToken");
    }

    private String loginBody(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }

    private String refreshBody(String refreshToken) {
        return "{\"refreshToken\":\"" + refreshToken + "\"}";
    }
}
