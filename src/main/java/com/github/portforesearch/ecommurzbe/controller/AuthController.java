package com.github.portforesearch.ecommurzbe.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.portforesearch.ecommurzbe.common.Token;
import com.github.portforesearch.ecommurzbe.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.exception.MissingTokenException;
import com.github.portforesearch.ecommurzbe.mapper.UserMapper;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.service.RegisterService;
import com.github.portforesearch.ecommurzbe.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final RegisterService registerService;
    private final UserService userService;

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

                String refreshToken = Token.generate(algorithm, user.getUsername(),
                        user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), 60,
                        request.getRequestURL().toString());
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
        User user = UserMapper.INSTANCE.loginRequestDtoToUser(userRequestDto);
        UserResponseDto userResponseDto = registerService.register(userRequestDto, user);
        return ResponseEntity.ok(userResponseDto);
    }
}
