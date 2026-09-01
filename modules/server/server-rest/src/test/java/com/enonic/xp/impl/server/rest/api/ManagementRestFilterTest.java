package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.vhost.VirtualHost;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManagementRestFilterTest
{
    private ManagementRestFilter filter;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterChain chain;

    @BeforeEach
    void setUp()
    {
        filter = new ManagementRestFilter();
        request = mock( HttpServletRequest.class );
        response = mock( HttpServletResponse.class );
        chain = mock( FilterChain.class );
    }

    @Test
    void noVirtualHost()
        throws Exception
    {
        when( request.getPathInfo() ).thenReturn( "/repo/list" );

        filter.doFilter( request, response, chain );

        verify( chain ).doFilter( request, response );
    }

    @Test
    void restEnabledByDefault()
        throws Exception
    {
        vhost( Map.of() );
        when( request.getPathInfo() ).thenReturn( "/repo/list" );

        filter.doFilter( request, response, chain );

        verify( chain ).doFilter( request, response );
    }

    @Test
    void restDisabledBlocksLegacyPaths()
        throws Exception
    {
        vhost( Map.of( ManagementRestFilter.REST_ENABLED, "false" ) );
        when( request.getPathInfo() ).thenReturn( "/repo/list" );

        filter.doFilter( request, response, chain );

        verify( response ).sendError( HttpServletResponse.SC_NOT_FOUND );
        verify( chain, never() ).doFilter( any(), any() );
    }

    @Test
    void restDisabledKeepsUniversalApis()
        throws Exception
    {
        vhost( Map.of( ManagementRestFilter.REST_ENABLED, "false" ) );
        when( request.getPathInfo() ).thenReturn( "/server:repo" );

        filter.doFilter( request, response, chain );

        verify( chain ).doFilter( request, response );
    }

    private void vhost( final Map<String, String> context )
    {
        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getContext() ).thenReturn( context );
        // the attribute VirtualHostFilter stores and VirtualHostHelper reads back
        when( request.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );
    }
}
