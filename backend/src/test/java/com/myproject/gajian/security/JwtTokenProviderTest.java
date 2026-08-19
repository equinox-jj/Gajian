package com.myproject.gajian.security;

import com.myproject.gajian.auth.entity.AppUser;
import com.myproject.gajian.auth.entity.Role;
import com.myproject.gajian.common.constant.JwtConstants;
import com.myproject.gajian.config.JwtConfig;
import com.myproject.gajian.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-that-is-at-least-32-bytes-long";
    private static final String ISSUER = "gajian-test";
    private static final Duration TTL = Duration.ofMinutes(15);

    private JwtTokenProvider jwtTokenProvider;
    private JwtDecoder jwtDecoder;
    private AppUser user;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(SECRET, ISSUER, TTL);
        JwtConfig jwtConfig = new JwtConfig();
        SecretKey secretKey = jwtConfig.jwtSecretKey(properties);

        jwtDecoder = jwtConfig.jwtDecoder(secretKey);
        jwtTokenProvider = new JwtTokenProvider(jwtConfig.jwtEncoder(secretKey), properties);
        user = AppUser.builder()
                .id(UUID.randomUUID())
                .email("joshua@example.com")
                .password("irrelevant-hash")
                .role(Role.ADMIN)
                .active(true)
                .build();
    }

    @Test
    void generatesTokenWithUserIdAsSubject() {
        // Arrange

        // Act
        AccessToken accessToken = jwtTokenProvider.generateAccessToken(user);

        // Assert
        Jwt decoded = jwtDecoder.decode(accessToken.value());
        assertThat(decoded.getSubject()).isEqualTo(user.getId().toString());
    }

    @Test
    void generatesTokenCarryingEmailAndRoleClaims() {
        // Arrange

        // Act
        AccessToken accessToken = jwtTokenProvider.generateAccessToken(user);

        // Assert
        Jwt decoded = jwtDecoder.decode(accessToken.value());
        assertThat(decoded.getClaimAsString(JwtConstants.CLAIM_EMAIL)).isEqualTo("joshua@example.com");
        assertThat(decoded.getClaimAsString(JwtConstants.CLAIM_ROLE)).isEqualTo("ADMIN");
    }

    @Test
    void generatesTokenIssuedByConfiguredIssuer() {
        // Arrange

        // Act
        AccessToken accessToken = jwtTokenProvider.generateAccessToken(user);

        // Assert
        Jwt decoded = jwtDecoder.decode(accessToken.value());
        assertThat(decoded.getClaimAsString(JwtClaimNames.ISS)).isEqualTo(ISSUER);
    }

    @Test
    void generatesTokenExpiringAfterConfiguredTtl() {
        // Arrange

        // Act
        AccessToken accessToken = jwtTokenProvider.generateAccessToken(user);

        // Assert
        Jwt decoded = jwtDecoder.decode(accessToken.value());
        assertThat(Duration.between(decoded.getIssuedAt(), decoded.getExpiresAt())).isEqualTo(TTL);
    }

    @Test
    void reportsTtlInSecondsAlongsideToken() {
        // Arrange

        // Act
        AccessToken accessToken = jwtTokenProvider.generateAccessToken(user);

        // Assert
        assertThat(accessToken.expiresInSeconds()).isEqualTo(900L);
    }

    @Test
    void signsTokenWithHmacSha256() {
        // Arrange

        // Act
        AccessToken accessToken = jwtTokenProvider.generateAccessToken(user);

        // Assert
        Jwt decoded = jwtDecoder.decode(accessToken.value());
        assertThat(decoded.getHeaders()).containsEntry("alg", JwsAlgorithms.HS256);
    }
}
