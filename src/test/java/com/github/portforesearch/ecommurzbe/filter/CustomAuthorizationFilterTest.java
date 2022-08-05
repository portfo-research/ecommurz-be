package com.github.portforesearch.ecommurzbe.filter;


import com.auth0.jwt.algorithms.Algorithm;
import com.github.portforesearch.ecommurzbe.common.Token;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AuthorizationServiceException;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

class CustomAuthorizationFilterTest {

    @Test
    public void doFilterInternal_withAuthLoginServlet_thenGenerateToken() throws ServletException, IOException {
        //GIVEN
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setServletPath("/auth/login");
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain();

        //WHEN
        new CustomAuthorizationFilter().doFilterInternal(mockHttpServletRequest, mockHttpServletResponse,
                mockFilterChain);

        //THEN
        assertNotNull(mockFilterChain.getRequest());
        assertNotNull(mockFilterChain.getResponse());
    }

    @Test
    public void doFilterInternal_withAuthTokenRefreshServlet_thenGenerateToken() throws ServletException,
            IOException {
        //GIVEN
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setServletPath("/auth/token/refresh");
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain();


        //WHEN
        new CustomAuthorizationFilter().doFilterInternal(mockHttpServletRequest, mockHttpServletResponse,
                mockFilterChain);

        //THEN
        assertNotNull(mockFilterChain.getRequest());
        assertNotNull(mockFilterChain.getResponse());
    }

    @Test
    public void doFilterInternal_WhenValidateToken_thenThrowAuthorizationServiceException() throws ServletException,
            IOException {
        //GIVEN
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setServletPath("/user/create");
        mockHttpServletRequest.addHeader(AUTHORIZATION, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
                ".eyJzdWIiOiJmYXNjYWxzaiIsInJvbGVzIjpbIkNVU1RPTUVSIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9hdXRoL2xvZ2luIiwiZXhwIjoxNjU5NzIzNzM5fQ.UBD_ZxTQz4fefKfX5Ve9CLseixp5ZpkxvqjzAFTLiCIffKymN6HZQqz4Vfro_pjZzf_qvI6RjCIH64v62tygDg");
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain();

        //WHEN
        AuthorizationServiceException authorizationServiceException = assertThrows(AuthorizationServiceException.class,
                () -> new CustomAuthorizationFilter().doFilterInternal(mockHttpServletRequest, mockHttpServletResponse,
                        mockFilterChain));
        //THEN
        assertEquals("Invalid bearer format", authorizationServiceException.getMessage());
    }

    @Test
    public void doFilterInternal_WhenValidateToken_thenReturnSuccess() throws IOException {
        //GIVEN
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setServletPath("/user/create");
        mockHttpServletRequest.addHeader(AUTHORIZATION, "Bearer " + generateToken());
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain();

        //WHEN
        new CustomAuthorizationFilter().doFilterInternal(mockHttpServletRequest, mockHttpServletResponse,
                mockFilterChain);
        //THEN
        assertNotNull(mockFilterChain.getRequest());
        assertNotNull(mockFilterChain.getResponse());
    }


    private String generateToken() {
        User user = new User();
        user.setUsername("username");

        Algorithm algorithm = Algorithm.HMAC512("secretKey".getBytes());

        String token = Token.generate(algorithm, user.getUsername(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()), 60, "/api/test");
        return token;
    }
}
