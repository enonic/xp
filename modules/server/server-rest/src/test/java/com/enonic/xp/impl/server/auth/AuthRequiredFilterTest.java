package com.enonic.xp.impl.server.auth;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.net.HttpHeaders;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthRequiredFilterTest
{
    private AuthRequiredFilter authRequiredFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp()
    {
        authRequiredFilter = new AuthRequiredFilter();
    }

    @Test
    void doHandle_whenUserPrincipalIsNull_sendsUnauthorizedError()
        throws Exception
    {
        when( request.getUserPrincipal() ).thenReturn( null );

        authRequiredFilter.doHandle( request, response, filterChain );

        verify( response ).addHeader( HttpHeaders.WWW_AUTHENTICATE, "Basic" );
        verify( response ).addHeader( HttpHeaders.WWW_AUTHENTICATE, "Bearer" );
        verify( response ).sendError( HttpServletResponse.SC_UNAUTHORIZED );
        verify( filterChain, never() ).doFilter( request, response );
    }

    @Test
    void doHandle_whenUserPrincipalIsNotNull_proceedsWithFilterChain()
        throws Exception
    {
        when( request.getUserPrincipal() ).thenReturn( () -> "user" );

        authRequiredFilter.doHandle( request, response, filterChain );

        verify( response, never() ).addHeader( anyString(), anyString() );
        verify( response, never() ).sendError( anyInt() );
        verify( filterChain ).doFilter( request, response );
    }
}
