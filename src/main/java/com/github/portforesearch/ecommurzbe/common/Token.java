package com.github.portforesearch.ecommurzbe.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Token {

    public static String generate(Algorithm algorithm, String username, List<String> listRole, int minutes, String requestUrl) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + minutes * 60 * 1000))
                .withIssuer(requestUrl)
                .withClaim("roles", listRole).sign(algorithm);
    }
}
