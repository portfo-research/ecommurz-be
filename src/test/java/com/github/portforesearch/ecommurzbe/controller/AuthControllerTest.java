package com.github.portforesearch.ecommurzbe.controller;


import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.service.AuthService;
import com.github.portforesearch.ecommurzbe.service.UserService;
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

import static org.hamcrest.Matchers.*;
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
    public void setup() {
        //Init MockMvc Object and build
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void refreshToken() throws Exception {
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
}
