package com.github.portforesearch.ecommurzbe.controller;


import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.portforesearch.ecommurzbe.common.Token;
import com.github.portforesearch.ecommurzbe.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.service.RegisterService;
import com.github.portforesearch.ecommurzbe.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.Collections;
import java.util.stream.Collectors;

import static com.github.portforesearch.ecommurzbe.constant.RoleConstant.ROLE_BUYER;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(AuthController.class)
class AuthControllerTest {
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "passWord";
    public static final String EMAIL = "email@email.com";
    private final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
            ".eyJzdWIiOiJmYXNjYWwiLCJyb2xlcyI6WyJST0xFX0dFTkVSQUxfVVNFUiIsIlJPTEVfU1VQRVJfQURNSU4iXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvdG9rZW4vcmVmcmVzaCIsImV4cCI6MTY1NjY5NjEyNX0.GxtP-ygwdAH0ON3GUEyrm9Hn-6MG_qmjJbWnBDHAV4UFC2X6RPR39tCr7h27J5uhnMAUmn5X0QyLeLnQs68Mhw";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private RegisterService registerService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @BeforeEach()
    void setup() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void refreshTokenWithBearer() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/auth/token/refresh")
                .servletPath("/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + TOKEN);

        User user = new User();
        user.setUsername(USERNAME);
        when(userService.findByUsername(anyString())).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(jsonPath("$.token", is(notNullValue())));
        resultActions.andExpect(jsonPath("$.token", is(not(TOKEN))));
    }

    @Test
    void refreshTokenWithoutBearer() {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/auth/token/refresh")
                .servletPath("/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, TOKEN);

        User user = new User();
        user.setUsername(USERNAME);
        when(userService.findByUsername(anyString())).thenReturn(user);

        Throwable exception = assertThrows(NestedServletException.class, () -> mockMvc.perform(requestBuilder));

        Assertions.assertEquals(exception.getMessage(), "Request processing failed; nested exception is com.github" +
                ".portforesearch.ecommurzbe.exception.MissingTokenException: Refresh token is missing");
    }

    @Test
    void refreshTokenStillValid() throws Exception {
        User user = new User();
        user.setUsername(USERNAME);

        Algorithm algorithm = Algorithm.HMAC512("secretKey".getBytes());

        String refreshToken = Token.generate(algorithm, user.getUsername(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), 60, "/api/test");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/auth/token/refresh")
                .servletPath("/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + refreshToken);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(jsonPath("$.token", is(refreshToken)));
    }

    @Test
    void registerSuccess() throws Exception {
        User user = new User();
        user.setUsername(USERNAME);

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername(USERNAME);
        userRequestDto.setPassword(PASSWORD);
        userRequestDto.setEmail(EMAIL);
        userRequestDto.setRole(Collections.singletonList(ROLE_BUYER));

        String message = String.format("User with username %s has been created with role %s",
                user.getUsername(), userRequestDto.getRole().toString());

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setMessage(message);
        userResponseDto.setUsername(USERNAME);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(userRequestDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        when(registerService.register(any(UserRequestDto.class), any(User.class))).thenReturn(userResponseDto);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.content().json("{" +
                "\"username\":" + userRequestDto.getUsername() +
                ",\"message\":\"" + message +
                "\"}"));
    }

    @Test
    void registerDuplicateUsername() throws Exception {
        User user = new User();
        user.setUsername(USERNAME);

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername(USERNAME);
        userRequestDto.setPassword(PASSWORD);
        userRequestDto.setEmail(EMAIL);
        userRequestDto.setRole(Collections.singletonList(ROLE_BUYER));

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(userRequestDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);


        when(userService.findByUsername(anyString())).thenReturn(user);
        when(userService.save(any(User.class))).thenReturn(user);
        try {
            mockMvc.perform(requestBuilder);
        } catch (NestedServletException e) {
            Assertions.assertEquals(e.getMessage(), String.format("Request processing failed; nested exception is com" +
                    ".github.portforesearch.ecommurzbe.exception.DuplicateUserException: User with username %s " +
                    "already exist", user.getUsername()));
        }
    }

    @Test
    void registerDuplicateEmail() throws Exception {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername(USERNAME);
        userRequestDto.setPassword(PASSWORD);
        userRequestDto.setEmail(EMAIL);
        userRequestDto.setRole(Collections.singletonList(ROLE_BUYER));

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(userRequestDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);


        when(userService.findByEmail(anyString())).thenReturn(user);
        when(userService.save(Mockito.any(User.class))).thenReturn(user);
        try {
            mockMvc.perform(requestBuilder);
        } catch (NestedServletException e) {
            Assertions.assertEquals(e.getMessage(), String.format("Request processing failed; nested exception is com" +
                    ".github.portforesearch.ecommurzbe.exception.DuplicateEmailException: User with email %s already " +
                    "exist", user.getEmail()));
        }
    }
}
