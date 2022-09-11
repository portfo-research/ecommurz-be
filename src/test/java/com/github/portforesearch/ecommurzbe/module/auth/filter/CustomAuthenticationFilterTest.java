package com.github.portforesearch.ecommurzbe.module.auth.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class CustomAuthenticationFilterTest {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    @Mock
    AuthenticationManager authenticationManager;

    ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationTokenArgumentCaptor =
            ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void attemptAuthentication_ThenReturnSuccess() {
        //GIVEN
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ADMIN");

        User user = new User(USERNAME, PASSWORD, List.of(simpleGrantedAuthority));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user, PASSWORD);

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager,
                "secretKey");

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter(USERNAME, USERNAME);
        mockHttpServletRequest.addParameter(PASSWORD, PASSWORD);
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        when(authenticationManager.authenticate(any())).thenReturn(usernamePasswordAuthenticationToken);

        //WHEN
        customAuthenticationFilter.attemptAuthentication(mockHttpServletRequest, mockHttpServletResponse);


        //THEN
        verify(authenticationManager, times(2)).authenticate(authenticationTokenArgumentCaptor.capture());
        List<UsernamePasswordAuthenticationToken> usernamePasswordAuthenticationTokenValue =
                authenticationTokenArgumentCaptor.getAllValues();

        assertEquals(USERNAME, usernamePasswordAuthenticationTokenValue.get(0).getName());
        assertEquals(PASSWORD, usernamePasswordAuthenticationTokenValue.get(0).getCredentials());
        assertEquals(USERNAME, usernamePasswordAuthenticationTokenValue.get(1).getName());
        assertEquals(PASSWORD, usernamePasswordAuthenticationTokenValue.get(1).getCredentials());
    }

    @Test
    void successfulAuthentication_thenReturnSuccess() throws IOException {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ADMIN");

        User user = new User(USERNAME, PASSWORD, List.of(simpleGrantedAuthority));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user, PASSWORD);

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager,
                "secretKey");
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter(USERNAME, USERNAME);
        mockHttpServletRequest.addParameter(PASSWORD, PASSWORD);
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        MockFilterChain mockFilterChain = new MockFilterChain();

        customAuthenticationFilter.successfulAuthentication(mockHttpServletRequest, mockHttpServletResponse,
                mockFilterChain, usernamePasswordAuthenticationToken);

        assertEquals(APPLICATION_JSON_VALUE, mockHttpServletResponse.getContentType());
    }
}