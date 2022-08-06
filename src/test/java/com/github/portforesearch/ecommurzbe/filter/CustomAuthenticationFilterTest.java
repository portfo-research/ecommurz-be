package com.github.portforesearch.ecommurzbe.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class CustomAuthenticationFilterTest {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    @InjectMocks
    CustomAuthenticationFilter customAuthenticationFilter;

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
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter(USERNAME, USERNAME);
        mockHttpServletRequest.addParameter(PASSWORD, PASSWORD);
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ADMIN");
        User user = new User(USERNAME, PASSWORD, List.of(simpleGrantedAuthority));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user, PASSWORD);

        when(authenticationManager.authenticate(any())).thenReturn(usernamePasswordAuthenticationToken);

        //WHEN
        customAuthenticationFilter.attemptAuthentication(mockHttpServletRequest, mockHttpServletResponse);


        //THEN
        verify(authenticationManager).authenticate(authenticationTokenArgumentCaptor.capture());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationTokenValue =
                authenticationTokenArgumentCaptor.getValue();

        assertEquals(USERNAME, usernamePasswordAuthenticationTokenValue.getName());
        assertEquals(PASSWORD, usernamePasswordAuthenticationTokenValue.getCredentials());
    }

    @Test
    void successfulAuthentication_thenReturnSuccess() throws IOException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter(USERNAME, USERNAME);
        mockHttpServletRequest.addParameter(PASSWORD, PASSWORD);
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ADMIN");
        User user = new User(USERNAME, PASSWORD, List.of(simpleGrantedAuthority));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user, PASSWORD);

        MockFilterChain mockFilterChain = new MockFilterChain();

        customAuthenticationFilter.successfulAuthentication(mockHttpServletRequest, mockHttpServletResponse,
                mockFilterChain, usernamePasswordAuthenticationToken);

        assertEquals(APPLICATION_JSON_VALUE, mockHttpServletResponse.getContentType());
    }
}