package com.myproject.gajian.security;

import com.myproject.gajian.auth.entity.AppUser;
import com.myproject.gajian.common.constant.JwtConstants;
import com.myproject.gajian.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public AccessToken generateAccessToken(AppUser user) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(jwtProperties.accessTokenTtl()))
                .claim(JwtConstants.CLAIM_EMAIL, user.getEmail())
                .claim(JwtConstants.CLAIM_ROLE, user.getRole().name())
                .build();

        // NimbusJwtEncoder defaults the header to RS256 and then fails to select an HMAC key.
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String value = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new AccessToken(value, jwtProperties.accessTokenTtl().toSeconds());
    }
}
