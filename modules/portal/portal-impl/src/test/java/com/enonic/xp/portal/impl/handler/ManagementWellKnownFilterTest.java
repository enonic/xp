package com.enonic.xp.portal.impl.handler;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.portal.impl.idprovider.OAuthMetadataService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.serializer.WebSerializerService;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ManagementWellKnownFilterTest
{
    private final OAuthMetadataService metadata = mock( OAuthMetadataService.class );

    private final WebSerializerService webSerializerService = mock( WebSerializerService.class );

    private final ExceptionRenderer exceptionRenderer = mock( ExceptionRenderer.class );

    private final HttpServletRequest req = mock( HttpServletRequest.class );

    private final HttpServletResponse res = mock( HttpServletResponse.class );

    private final FilterChain chain = mock( FilterChain.class );

    private final VirtualHost mapping = mock( VirtualHost.class );

    private ManagementWellKnownFilter filter;

    @BeforeEach
    void setUp()
    {
        final Map<String, Object> attributes = new HashMap<>();
        doAnswer( i -> attributes.put( i.getArgument( 0 ), i.getArgument( 1 ) ) ).when( req ).setAttribute( any(), any() );
        when( req.getAttribute( any() ) ).thenAnswer( i -> attributes.get( i.<String>getArgument( 0 ) ) );
        when( req.getMethod() ).thenReturn( "GET" );
        filter = new ManagementWellKnownFilter( metadata, webSerializerService, exceptionRenderer );
    }

    private WebRequest webRequest( final String path )
    {
        when( req.getPathInfo() ).thenReturn( path );
        VirtualHostHelper.setVirtualHost( req, mapping );
        VirtualHostHelper.setRequestedPath( req, path );
        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( path );
        webRequest.setRawRequest( req );
        webRequest.setVirtualHost( mapping );
        when( webSerializerService.request( req ) ).thenReturn( webRequest );
        return webRequest;
    }

    @Test
    void serves_the_protected_resource_metadata_before_authentication()
        throws Exception
    {
        final WebRequest webRequest = webRequest( "/.well-known/oauth-protected-resource/repo:list" );
        when( metadata.protectedResource( webRequest, "/repo:list" ) ).thenReturn( Map.of( "resource", "https://mgmt/repo:list" ) );

        filter.doFilter( req, res, chain );

        final ArgumentCaptor<WebResponse> response = ArgumentCaptor.forClass( WebResponse.class );
        verify( webSerializerService ).response( eq( webRequest ), response.capture(), eq( res ) );
        assertThat( response.getValue().getStatus() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getValue().getBody() ).isEqualTo( Map.of( "resource", "https://mgmt/repo:list" ) );
        verifyNoInteractions( chain );
    }

    @Test
    void nothing_to_describe_is_rendered_as_not_found()
        throws Exception
    {
        final WebRequest webRequest = webRequest( "/.well-known/oauth-protected-resource" );
        when( metadata.protectedResource( webRequest, "/" ) ).thenReturn( null );
        final WebResponse notFound = WebResponse.create().status( HttpStatus.NOT_FOUND ).build();
        when( exceptionRenderer.render( eq( webRequest ), any( WebException.class ) ) ).thenReturn( notFound );

        filter.doFilter( req, res, chain );

        verify( webSerializerService ).response( webRequest, notFound, res );
        verifyNoInteractions( chain );
    }

    @Test
    void other_paths_methods_and_the_authorization_server_metadata_go_on()
        throws Exception
    {
        when( req.getPathInfo() ).thenReturn( "/repo:list" );
        VirtualHostHelper.setVirtualHost( req, mapping );
        filter.doFilter( req, res, chain );

        when( req.getPathInfo() ).thenReturn( "/.well-known/oauth-authorization-server/_/idprovider/system" );
        filter.doFilter( req, res, chain );

        when( req.getPathInfo() ).thenReturn( "/.well-known/oauth-protected-resource" );
        when( req.getMethod() ).thenReturn( "POST" );
        filter.doFilter( req, res, chain );

        verify( chain, times( 3 ) ).doFilter( req, res );
        verify( webSerializerService, never() ).request( any() );
    }
}
