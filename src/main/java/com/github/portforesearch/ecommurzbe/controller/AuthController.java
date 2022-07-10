package com.github.portforesearch.ecommurzbe.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.portforesearch.ecommurzbe.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.exception.DuplicateEmailException;
import com.github.portforesearch.ecommurzbe.exception.DuplicateUserException;
import com.github.portforesearch.ecommurzbe.exception.MissingTokenException;
import com.github.portforesearch.ecommurzbe.mapper.UserMapper;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.service.AuthService;
import com.github.portforesearch.ecommurzbe.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String oldToken = authorizationHeader.substring("Bearer ".length());
            Algorithm algorithm = Algorithm.HMAC512("secretKey".getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = JWT.decode(oldToken);
            try {
                verifier.verify(oldToken);
                HashMap<String, String> tokens = new HashMap<>();
                tokens.put("token", oldToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (TokenExpiredException e) {
                String username = decodedJWT.getSubject();
                User user = userService.findByUsername(username);

                String refreshToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                HashMap<String, String> tokens = new HashMap<>();
                tokens.put("token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }
        } else {
            throw new MissingTokenException("Refresh token is missing");
        }
    }

    @PostMapping("register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto userRequestDto) {
        User user = userService.save(UserMapper.INSTANCE.loginRequestDtoToUser(userRequestDto));
        userRequestDto.getRole().forEach(role -> authService.addRoleToUser(user.getUsername(), role));

        if (userService.findByUsername(user.getUsername()) != null) {
            throw new DuplicateUserException(String.format("User with username %s already exist", user.getUsername()));
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            throw new DuplicateEmailException(String.format("User with email %s already exist", user.getEmail()));
        }

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setMessage(String.format("User with username %s has been created with role %s",
                user.getUsername(), userRequestDto.getRole().toString()));

        return ResponseEntity.ok(userResponseDto);
    }

}
