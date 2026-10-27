package com.enonic.xp.portal.impl.idprovider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdProviderResponseWrapperTest
{

    private IdProviderControllerService idProviderControllerService;

    private IdProviderResponseWrapper idProviderResponseWrapper;

    @BeforeEach
    void setup()
        throws IOException
    {
        this.idProviderControllerService = Mockito.mock( IdProviderControllerService.class );
        Mockito.when( idProviderControllerService.execute( Mockito.any() ) ).thenReturn( PortalResponse.create().build() );
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );

        // unrestricted default id provider, as the default vhost provides
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getIdProviders() ).thenReturn( Map.of( IdProviderKey.system(), idProvider() ) );
        Mockito.when( httpServletRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        this.idProviderResponseWrapper =
            new IdProviderResponseWrapper( idProviderControllerService, httpServletRequest, httpServletResponse );
    }

    @Test
    void testSetStatus()
        throws IOException
    {
        idProviderResponseWrapper.setStatus( 404 );
        idProviderResponseWrapper.complete();
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        idProviderResponseWrapper.setStatus( 403 );
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        idProviderResponseWrapper.complete();
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
        idProviderResponseWrapper.setStatus( 403 );
        idProviderResponseWrapper.complete();
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
    }

    @Test
    void testSendError()
        throws IOException
    {
        idProviderResponseWrapper.sendError( 404 );
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        idProviderResponseWrapper.sendError( 403 );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
        idProviderResponseWrapper.sendError( 403 );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
    }

    @Test
    void testSendErrorWithMessage()
        throws IOException
    {
        idProviderResponseWrapper.sendError( 404, "message" );
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        idProviderResponseWrapper.sendError( 403, "message" );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
        idProviderResponseWrapper.sendError( 403, "message" );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
    }

    @Test
    void testHandle401_gatedByLoginFlow()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );

        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getIdProviders() ).thenReturn( Map.of( IdProviderKey.system(), idProvider( IdProviderFlow.AUTOLOGIN ) ) );
        Mockito.when( httpServletRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        final IdProviderResponseWrapper responseWrapper =
            new IdProviderResponseWrapper( idProviderControllerService, httpServletRequest, httpServletResponse );

        // the default id provider does not have the login flow, so handle401 must not run
        responseWrapper.sendError( 401 );
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        Mockito.verify( httpServletResponse ).sendError( 401 );

        Mockito.when( virtualHost.getIdProviders() )
            .thenReturn( Map.of( IdProviderKey.system(), idProvider( IdProviderFlow.LOGIN, IdProviderFlow.AUTOLOGIN ) ) );

        responseWrapper.sendError( 401 );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
    }

    @Test
    void testHandle401_noIdProviders()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );

        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getIdProviders() ).thenReturn( Map.of() );
        Mockito.when( httpServletRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        final IdProviderResponseWrapper responseWrapper =
            new IdProviderResponseWrapper( idProviderControllerService, httpServletRequest, httpServletResponse );

        responseWrapper.sendError( 401 );

        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        Mockito.verify( httpServletResponse ).sendError( 401 );
    }

    @Test
    void testAuthenticated403_notIntercepted()
        throws Exception
    {
        final User user =
            User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).displayName( "User 1" ).login( "user1" ).build();
        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.create().user( user ).principals( RoleKeys.AUTHENTICATED ).build();

        ContextBuilder.create().authInfo( authenticationInfo ).build().callWith( () -> {
            idProviderResponseWrapper.sendError( 403 );
            Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
            return null;
        } );
    }

    private static VirtualHostIdProvider idProvider( final String... flows )
    {
        return VirtualHostIdProvider.create().flows( Set.of( flows ) ).build();
    }

    @Test
    void a_status_set_before_its_bearer_challenge_keeps_the_login_page_out()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getIdProviders() ).thenReturn( Map.of( IdProviderKey.system(), idProvider( IdProviderFlow.LOGIN ) ) );
        Mockito.when( virtualHost.getSource() ).thenReturn( "/" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/" );
        final Map<String, Object> attributes = new HashMap<>();
        Mockito.doAnswer( i -> attributes.put( i.getArgument( 0 ), i.getArgument( 1 ) ) ).when( httpServletRequest ).setAttribute( Mockito.any(), Mockito.any() );
        Mockito.when( httpServletRequest.getAttribute( Mockito.any() ) ).thenAnswer( i -> attributes.get( i.<String>getArgument( 0 ) ) );
        VirtualHostHelper.setVirtualHost( httpServletRequest, virtualHost );
        VirtualHostHelper.setRequestedPath( httpServletRequest, "/api/mcp" );
        Mockito.when( httpServletRequest.getScheme() ).thenReturn( "https" );
        Mockito.when( httpServletRequest.getServerName() ).thenReturn( "h" );
        Mockito.when( httpServletRequest.getServerPort() ).thenReturn( 443 );
        final IdProviderResponseWrapper wrapper =
            new IdProviderResponseWrapper( idProviderControllerService, httpServletRequest, httpServletResponse );

        wrapper.setStatus( 401 );
        wrapper.setHeader( "WWW-Authenticate", "Bearer" );
        wrapper.complete();

        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        Mockito.verify( httpServletResponse ).setStatus( 401 );
        Mockito.verify( httpServletResponse ).setHeader( "WWW-Authenticate",
                                                         "Bearer resource_metadata=\"https://h/.well-known/oauth-protected-resource/api/mcp\"" );
    }

    @Test
    void testGetWriter()
        throws IOException
    {
        assertNull( idProviderResponseWrapper.getWriter() );
        idProviderResponseWrapper.setStatus( 403 );
        assertNotNull( idProviderResponseWrapper.getWriter() );
    }

    @Test
    void testGetOutputStream()
        throws IOException
    {
        ServletOutputStream outputStream = idProviderResponseWrapper.getOutputStream();
        assertNull( outputStream );

        idProviderResponseWrapper.setStatus( 403 );

        outputStream = idProviderResponseWrapper.getOutputStream();
        assertNotNull( outputStream );
        assertTrue( outputStream.isReady() );
        outputStream.setWriteListener( null );
        outputStream.write( 0 );
    }



    @Test
    void a_bearer_challenge_keeps_the_login_page_out_and_names_the_resource_metadata()
        throws IOException
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final VirtualHost virtualHost = new VirtualHostMapping( "m", "localhost", "/", "/", VirtualHostIdProvidersMapping.create()
            .addIdProviderWithSettings( IdProviderKey.system(), idProvider() )
            .build(), 0 );
        final Map<String, Object> attributes = new HashMap<>();
        Mockito.doAnswer( i -> attributes.put( i.getArgument( 0 ), i.getArgument( 1 ) ) ).when( httpServletRequest ).setAttribute( Mockito.any(), Mockito.any() );
        Mockito.when( httpServletRequest.getAttribute( Mockito.any() ) ).thenAnswer( i -> attributes.get( i.<String>getArgument( 0 ) ) );
        VirtualHostHelper.setVirtualHost( httpServletRequest, virtualHost );
        VirtualHostHelper.setRequestedPath( httpServletRequest, "/api/mcp" );
        Mockito.when( httpServletRequest.getScheme() ).thenReturn( "https" );
        Mockito.when( httpServletRequest.getServerName() ).thenReturn( "example.com" );
        Mockito.when( httpServletRequest.getServerPort() ).thenReturn( 443 );
        final IdProviderResponseWrapper responseWrapper =
            new IdProviderResponseWrapper( idProviderControllerService, httpServletRequest, httpServletResponse );

        responseWrapper.setHeader( "WWW-Authenticate", "Bearer" );
        responseWrapper.sendError( 401 );

        Mockito.verify( httpServletResponse ).setHeader( "WWW-Authenticate",
                                                         "Bearer resource_metadata=\"https://example.com/.well-known/oauth-protected-resource/api/mcp\"" );
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        Mockito.verify( httpServletResponse ).sendError( 401 );

        final IdProviderResponseWrapper another =
            new IdProviderResponseWrapper( idProviderControllerService, httpServletRequest, httpServletResponse );
        another.addHeader( "WWW-Authenticate", "Bearer realm=\"x\", resource_metadata=\"https://example.com/own\"" );
        Mockito.verify( httpServletResponse ).addHeader( "WWW-Authenticate", "Bearer realm=\"x\", resource_metadata=\"https://example.com/own\"" );
        another.setHeader( "WWW-Authenticate", "Basic realm=\"x\"" );
        Mockito.verify( httpServletResponse ).setHeader( "WWW-Authenticate", "Basic realm=\"x\"" );
        another.setStatus( 401 );
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
    }
}
