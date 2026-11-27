package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.security.auth.AuthenticationInfo;
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
    void restDisabledKeepsTheWellKnownDocuments()
        throws Exception
    {
        vhost( Map.of( ManagementRestFilter.REST_ENABLED, "false" ) );
        when( request.getPathInfo() ).thenReturn( "/.well-known/oauth-protected-resource/repo/list" );

        filter.doFilter( request, response, chain );

        verify( chain ).doFilter( request, response );
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

    @Test
    void restDisabledBlocksRootWithoutPath()
        throws Exception
    {
        vhost( Map.of( ManagementRestFilter.REST_ENABLED, "false" ) );
        when( request.getPathInfo() ).thenReturn( null );

        filter.doFilter( request, response, chain );

        verify( response ).sendError( HttpServletResponse.SC_NOT_FOUND );
    }

    @Test
    void restEnabledExplicitly()
        throws Exception
    {
        vhost( Map.of( ManagementRestFilter.REST_ENABLED, "true" ) );
        when( request.getPathInfo() ).thenReturn( "/repo/list" );

        filter.doFilter( request, response, chain );

        verify( chain ).doFilter( request, response );
    }

    private void withToken( final String... scope )
        throws Exception
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create()
            .user( User.create().key( PrincipalKey.from( "user:system:alice" ) ).login( "alice" ).build() )
            .accessToken( AccessTokenInfo.create().issuer( "xp" ).scope( List.of( scope ) ).build() )
            .build();
        ContextBuilder.copyOf( ContextAccessor.current() ).authInfo( authInfo ).build().callWith( () -> {
            filter.doFilter( request, response, chain );
            return null;
        } );
    }

    @Test
    void legacyPathsTakeATokenForEverything()
        throws Exception
    {
        when( request.getPathInfo() ).thenReturn( "/repo/list" );

        withToken( "admin" );

        verify( chain ).doFilter( request, response );
    }

    @Test
    void legacyPathsRefuseANarrowerToken()
        throws Exception
    {
        when( request.getPathInfo() ).thenReturn( "/repo/list" );

        withToken( "read", "repo" );

        verify( response ).setHeader( "WWW-Authenticate", "Bearer error=\"insufficient_scope\", scope=\"admin\"" );
        verify( response ).sendError( HttpServletResponse.SC_FORBIDDEN );
        verify( chain, never() ).doFilter( any(), any() );
    }

    @Test
    void legacyPathsRefuseATokenWithoutScopes()
        throws Exception
    {
        when( request.getPathInfo() ).thenReturn( null );

        withToken();

        verify( response ).sendError( HttpServletResponse.SC_FORBIDDEN );
    }

    @Test
    void universalApisCheckScopesThemselves()
        throws Exception
    {
        when( request.getPathInfo() ).thenReturn( "/server:repo" );

        withToken( "read:repo" );

        verify( chain ).doFilter( request, response );
    }

    @Test
    void restDisabledAnswersNotFoundWhateverTheToken()
        throws Exception
    {
        vhost( Map.of( ManagementRestFilter.REST_ENABLED, "false" ) );
        when( request.getPathInfo() ).thenReturn( "/repo/list" );

        withToken( "admin" );

        verify( response ).sendError( HttpServletResponse.SC_NOT_FOUND );
    }

    private void vhost( final Map<String, String> context )
    {
        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getContext() ).thenReturn( context );
        // the attribute VirtualHostFilter stores and VirtualHostHelper reads back
        when( request.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );
    }
}
