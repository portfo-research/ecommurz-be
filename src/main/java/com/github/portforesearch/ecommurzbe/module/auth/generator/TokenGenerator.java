package com.github.portforesearch.ecommurzbe.module.auth.generator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Generate token
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenGenerator {

    public static String generate(Algorithm algorithm, String username, List<String> listRole, int minutes, String requestUrl) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + (long) minutes * 60 * 1000))
                .withIssuer(requestUrl)
                .withClaim("roles", listRole).sign(algorithm);
    }
}
