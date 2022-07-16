package com.github.portforesearch.ecommurzbe.filter;


import com.auth0.jwt.algorithms.Algorithm;
import com.github.portforesearch.ecommurzbe.common.Token;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.stream.Collectors;

import static com.github.portforesearch.ecommurzbe.constant.RoleConstant.ROLE_BUYER;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class FilterTest {
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "password";
    private final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
            ".eyJzdWIiOiJmYXNjYWwiLCJyb2xlcyI6WyJST0xFX0dFTkVSQUxfVVNFUiIsIlJPTEVfU1VQRVJfQURNSU4iXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvdG9rZW4vcmVmcmVzaCIsImV4cCI6MTY1NjY5NjEyNX0.GxtP-ygwdAH0ON3GUEyrm9Hn-6MG_qmjJbWnBDHAV4UFC2X6RPR39tCr7h27J5uhnMAUmn5X0QyLeLnQs68Mhw";
    @MockBean
    UserRepo userRepo;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach()
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }

    @Test
    void loginSuccess() throws Exception {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordEncode = passwordEncoder.encode(PASSWORD);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/auth/login")
                .servletPath("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(USERNAME).password(PASSWORD).roles(ROLE_BUYER))
                .param("username", USERNAME)
                .param("password", PASSWORD)
                .header(AUTHORIZATION, "Bearer " + TOKEN);


        Role role = new Role();
        role.setName(ROLE_BUYER);

        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(passwordEncode);
        user.setRoles(Collections.singletonList(role));

        when(userRepo.findByUsernameAndRecordStatusId(anyString(), anyInt())).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(jsonPath("$.token", is(notNullValue())));
    }

    @Test
    void refreshTokenOldSuccess() throws Exception {
        Role role = new Role();
        role.setName(ROLE_BUYER);


        User user = new User();
        user.setUsername(USERNAME);
        user.setRoles(Collections.singletonList(role));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/auth/token/refresh")
                .servletPath("/auth/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(USERNAME).password(PASSWORD).roles(ROLE_BUYER))
                .param("username", USERNAME)
                .param("password", PASSWORD)
                .header(AUTHORIZATION, "Bearer " + TOKEN);

        when(userRepo.findByUsernameAndRecordStatusId(anyString(), anyInt())).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(jsonPath("$.token", is(notNullValue())));
    }


    @Test
    void doFilterInternalError() throws Exception {
        Role role = new Role();
        role.setName(ROLE_BUYER);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordEncode = passwordEncoder.encode(PASSWORD);

        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(passwordEncode);
        user.setRoles(Collections.singletonList(role));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .servletPath("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(USERNAME).password(PASSWORD).roles(ROLE_BUYER))
                .param("username", USERNAME)
                .param("password", PASSWORD)
                .header(AUTHORIZATION, "Bearer " + TOKEN);


        when(userRepo.findByUsernameAndRecordStatusId(anyString(), anyInt())).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(jsonPath("$.error_message", is(notNullValue())));
    }

    @Test
    void doFilterInternalErrorWithoutBearer() throws Exception {
        Role role = new Role();
        role.setName(ROLE_BUYER);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordEncode = passwordEncoder.encode(PASSWORD);

        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(passwordEncode);
        user.setRoles(Collections.singletonList(role));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .servletPath("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(USERNAME).password(PASSWORD).roles(ROLE_BUYER))
                .param("username", USERNAME)
                .param("password", PASSWORD)
                .header(AUTHORIZATION, TOKEN);


        when(userRepo.findByUsernameAndRecordStatusId(anyString(), anyInt())).thenReturn(user);
        mockMvc.perform(requestBuilder);
        assertTrue(true);
    }

    @Test
    void doFilterInternalSuccess() throws Exception {
        Role role = new Role();
        role.setName(ROLE_BUYER);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordEncode = passwordEncoder.encode(PASSWORD);

        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(passwordEncode);
        user.setRoles(Collections.singletonList(role));

        Algorithm algorithm = Algorithm.HMAC512("secretKey".getBytes());

        String refreshToken = Token.generate(algorithm, user.getUsername(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), 60, "/auth/register");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .servletPath("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(USERNAME).password(PASSWORD).roles(ROLE_BUYER))
                .param("username", USERNAME)
                .param("password", PASSWORD)
                .header(AUTHORIZATION, "Bearer " + refreshToken);


        when(userRepo.findByUsernameAndRecordStatusId(anyString(), anyInt())).thenReturn(user);

        mockMvc.perform(requestBuilder);
        assertTrue(true);
    }


}
