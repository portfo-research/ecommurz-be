package com.github.portforesearch.ecommurzbe.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.portforesearch.ecommurzbe.constant.RoleConstant;
import com.github.portforesearch.ecommurzbe.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.service.AuthService;
import com.github.portforesearch.ecommurzbe.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    AuthService authService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach()
    void setup() {
        //Init MockMvc Object and build
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void refreshTokenWithBearer() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
                ".eyJzdWIiOiJmYXNjYWwiLCJyb2xlcyI6WyJST0xFX0dFTkVSQUxfVVNFUiIsIlJPTEVfU1VQRVJfQURNSU4iXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvdG9rZW4vcmVmcmVzaCIsImV4cCI6MTY1NjY5NjEyNX0.GxtP-ygwdAH0ON3GUEyrm9Hn-6MG_qmjJbWnBDHAV4UFC2X6RPR39tCr7h27J5uhnMAUmn5X0QyLeLnQs68Mhw";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + token);

        User user = new User();
        user.setUsername("username");
        Mockito.when(userService.findByUsername(Mockito.anyString())).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.token", is(notNullValue())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.token", is(not(token))));
    }

    @Test
    void refreshTokenWithoutBearer() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
                ".eyJzdWIiOiJmYXNjYWwiLCJyb2xlcyI6WyJST0xFX0dFTkVSQUxfVVNFUiIsIlJPTEVfU1VQRVJfQURNSU4iXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvdG9rZW4vcmVmcmVzaCIsImV4cCI6MTY1NjY5NjEyNX0.GxtP-ygwdAH0ON3GUEyrm9Hn-6MG_qmjJbWnBDHAV4UFC2X6RPR39tCr7h27J5uhnMAUmn5X0QyLeLnQs68Mhw";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, token);

        User user = new User();
        user.setUsername("username");
        Mockito.when(userService.findByUsername(Mockito.anyString())).thenReturn(user);

        Throwable exception = assertThrows(NestedServletException.class, () -> mockMvc.perform(requestBuilder));

        Assertions.assertEquals(exception.getMessage(), "Request processing failed; nested exception is com.github" +
                ".portforesearch.ecommurzbe.exception.MissingTokenException: Refresh token is missing");
    }

    @Test
    void refreshTokenStillValid() throws Exception {
        User user = new User();
        user.setUsername("username");

        Algorithm algorithm = Algorithm.HMAC512("secretKey".getBytes());

        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .withIssuer("/api/test")
                .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .sign(algorithm);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + refreshToken);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.token", is(refreshToken)));
    }

    @Test
    void registerSuccess() throws Exception {
        User user = new User();
        user.setUsername("username");

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");
        userRequestDto.setPassword("password");
        userRequestDto.setEmail("email@email.com");
        userRequestDto.setRole(Collections.singletonList(RoleConstant.MANAGER));

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(userRequestDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        Mockito.when(userService.save(Mockito.any(User.class))).thenReturn(user);
        String message = String.format("User with username %s has been created with role %s",
                user.getUsername(), userRequestDto.getRole().toString());

        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.content().json("{" +
                "\"username\":"+ userRequestDto.getUsername()+
                ",\"message\":\""+ message+
                "\"}"));
    }
}
