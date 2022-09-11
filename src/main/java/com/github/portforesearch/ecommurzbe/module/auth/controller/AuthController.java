package com.github.portforesearch.ecommurzbe.module.auth.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.portforesearch.ecommurzbe.module.auth.generator.TokenGenerator;
import com.github.portforesearch.ecommurzbe.module.user.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.module.user.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.module.auth.exception.MissingTokenException;
import com.github.portforesearch.ecommurzbe.module.user.mapper.UserMapper;
import com.github.portforesearch.ecommurzbe.module.role.model.Role;
import com.github.portforesearch.ecommurzbe.module.user.model.User;
import com.github.portforesearch.ecommurzbe.module.auth.service.RegisterService;
import com.github.portforesearch.ecommurzbe.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller for authentication
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final RegisterService registerService;
    private final UserService userService;
    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    /**
     * Use for refreshing token when token expired
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = Objects.requireNonNull(request.getHeader(AUTHORIZATION), "Request is invalid");
        String requestUrl = Objects.requireNonNull(request.getRequestURL()).toString();
        if (authorizationHeader.startsWith("Bearer ")) {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecretKey.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            String oldToken = authorizationHeader.substring("Bearer ".length());
            HashMap<String, String> tokens = new HashMap<>();

            try {
                verifier.verify(oldToken);
                tokens.put("token", oldToken);

                response.setContentType(APPLICATION_JSON_VALUE);
                //Reuse old token when token still valid
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (TokenExpiredException e) {
                DecodedJWT decodedJWT = JWT.decode(oldToken);
                String username = decodedJWT.getSubject();

                User user = userService.findByUsername(username).orElseThrow(() -> new MissingTokenException("User " +
                        "not found"));

                List<String> listRole = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
                //Generate new Token
                String refreshToken = TokenGenerator.generate(algorithm, user.getUsername(), listRole, 60, requestUrl);

                tokens.put("token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }
        } else {
            throw new MissingTokenException("Refresh token is missing");
        }
    }

    /**
     * use for register new member
     *
     * @param userRequestDto
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto userRequestDto) {
        User user = UserMapper.INSTANCE.loginRequestDtoToUser(userRequestDto);
        UserResponseDto userResponseDto = registerService.register(userRequestDto, user);
        return ResponseEntity.ok(userResponseDto);
    }
}
