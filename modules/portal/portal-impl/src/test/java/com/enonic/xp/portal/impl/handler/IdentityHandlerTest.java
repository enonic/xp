package com.enonic.xp.portal.impl.handler;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.trace.TestTrace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IdentityHandlerTest
{
    IdentityHandler handler;

    PortalRequest request;

    String virtualHostKey;

    RedirectChecksumService redirectChecksumService;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        final IdProviderControllerService idProviderControllerService = mock( IdProviderControllerService.class );
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );

        final IdProviderKey myIdProvider = IdProviderKey.from( "myidprovider" );

        when( idProviderControllerService.execute( Mockito.any() ) ).thenAnswer( invocation -> {
            Object[] args = invocation.getArguments();
            final IdProviderControllerExecutionParams arg = (IdProviderControllerExecutionParams) args[0];
            if ( myIdProvider.equals( arg.getIdProviderKey() ) && arg.getFunctionName() == null )
            {
                return PortalResponse.create().build();
            }
            else if ( myIdProvider.equals( arg.getIdProviderKey() ) && "login".equals( arg.getFunctionName() ) )
            {
                return PortalResponse.create().build();
            }
            return null;
        } );

        redirectChecksumService = mock( RedirectChecksumService.class );

        this.handler = new IdentityHandler( idProviderControllerService, redirectChecksumService );

        this.request.setMethod( HttpMethod.GET );
        this.request.setRawPath( "/site/project/branch/_/idprovider/myidprovider" );
        this.request.setRawRequest( rawRequest );

        final VirtualHost virtualHost = mock( VirtualHost.class );

        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/site/project/branch" );
        when( virtualHost.getIdProviders() ).thenReturn( Map.of( myIdProvider, idProvider() ) );
        when( rawRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );
    }

    @Test
    void testOptions()
        throws Exception
    {
        final IdProviderControllerService idProviderControllerService = mock( IdProviderControllerService.class );
        final PortalResponse response = PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        when( idProviderControllerService.execute( Mockito.any() ) ).thenReturn( response );
        this.handler = new IdentityHandler( idProviderControllerService, mock() );

        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE,PATCH", res.getHeaders().get( "Allow" ) );
    }

    @Test
    void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setRawPath( "/_/idprovider/" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid idprovider url pattern", e.getMessage() );
        }
    }

    @Test
    void testHandle()
        throws Exception
    {
        final WebResponse portalResponse = this.handler.handle( this.request );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "/site/project/branch/_/idprovider/myidprovider", this.request.getContextPath() );
    }

    @Test
    void testHandle_recordsTraceAttributes()
        throws Exception
    {
        this.request.setPath( "/site/project/branch/_/idprovider/myidprovider" );
        this.request.setHost( "localhost" );

        // outside OSGi the @Traced wrapper is inert; a manually bound trace exercises the attribute enrichment code
        final TestTrace trace = TestTrace.of( "portalRequest" );
        final WebResponse portalResponse = Tracer.traceEx( trace, () -> this.handler.handle( this.request ) );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "/site/project/branch/_/idprovider/myidprovider", trace.get( "path" ) );
        assertEquals( "GET", trace.get( "method" ) );
        assertEquals( "localhost", trace.get( "host" ) );
        assertEquals( 200L, trace.get( "status" ) );
        assertEquals( "text/plain; charset=utf-8", trace.get( "type" ) );
        assertEquals( 0L, trace.get( "size" ) );
    }

    @Test
    void testHandle_redirect()
        throws Exception
    {
        this.request.setRawPath( "/site/project/branch/_/idprovider/myidprovider/login" );
        when( redirectChecksumService.verifyChecksum( "https://example.com", "some-good-checksum" ) ).thenReturn( true );

        this.request.getParams().put( "redirect", "https://example.com" );
        this.request.getParams().put( "_ticket", "some-good-checksum" );
        this.handler.handle( this.request );

        assertTrue( this.request.isValidTicket() );
    }

    @Test
    void testHandle_redirect_invalid()
        throws Exception
    {
        this.request.setRawPath( "/site/project/branch/_/idprovider/myidprovider/login" );
        when( redirectChecksumService.verifyChecksum( "https://example.com", "some-bad-checksum" ) ).thenReturn( false );

        this.request.getParams().put( "redirect", "https://example.com" );
        this.request.getParams().put( "_ticket", "some-bad-checksum" );
        this.handler.handle( this.request );

        assertFalse( this.request.isValidTicket() );
    }

    @Test
    void testHandleWithVirtualHostNotEnabled()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviders() ).thenReturn( Map.of( IdProviderKey.from( "otherEnabledIdProvider" ), idProvider() ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        try
        {
            this.handler.handle( this.request );
        }
        catch ( final WebException e )
        {
            assertEquals( "'myidprovider' id provider is forbidden", e.getMessage() );
        }
    }

    @Test
    void testHandleWithVirtualHostEnabled()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviders() ).thenReturn(
            Map.of( IdProviderKey.from( "otherEnabledIdProvider" ), idProvider(), IdProviderKey.from( "myidprovider" ), idProvider() ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        final WebResponse portalResponse = this.handler.handle( this.request );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "/site/project/branch/_/idprovider/myidprovider", this.request.getContextPath() );
    }

    @Test
    void testHandleWithEmptyVirtualHostIdProviderConfig()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviders() ).thenReturn( Map.of() );

        VirtualHostHelper.setVirtualHost( rawRequest, virtualHost );

        final WebResponse portalResponse = this.handler.handle( this.request );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "/site/project/branch/_/idprovider/myidprovider", this.request.getContextPath() );
    }

    @Test
    void testHandleWithLoginFlowDisabled()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();
        final IdProviderKey myIdProvider = IdProviderKey.from( "myidprovider" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/site/project/branch" );
        when( virtualHost.getIdProviders() )
            .thenReturn( Map.of( myIdProvider, idProvider( IdProviderFlow.AUTOLOGIN, IdProviderFlow.LOGOUT ) ) );
        when( rawRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        // the custom endpoints are always dispatched - the id provider app governs them itself
        final WebResponse portalResponse = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );

        // the login function is governed by the login flow
        this.request.setRawPath( "/site/project/branch/_/idprovider/myidprovider/login" );
        final WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.UNAUTHORIZED, ex.getStatus() );
        assertEquals( "'login' flow is disabled for 'myidprovider' id provider", ex.getMessage() );

        // logout is governed by its own flow: it passes the gate and reaches function dispatch
        this.request.setRawPath( "/site/project/branch/_/idprovider/myidprovider/logout" );
        final WebException notFound = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, notFound.getStatus() );
        assertEquals( "ID Provider function [logout] not found for id provider [myidprovider]", notFound.getMessage() );
    }

    @Test
    void testHandleWithLogoutFlowDisabled()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();
        final IdProviderKey myIdProvider = IdProviderKey.from( "myidprovider" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/site/project/branch" );
        when( virtualHost.getIdProviders() )
            .thenReturn( Map.of( myIdProvider, idProvider( IdProviderFlow.LOGIN, IdProviderFlow.AUTOLOGIN ) ) );
        when( rawRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        // the interactive surface still works
        final WebResponse portalResponse = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );

        // but logout is disabled independently
        this.request.setRawPath( "/site/project/branch/_/idprovider/myidprovider/logout" );
        final WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.UNAUTHORIZED, ex.getStatus() );
        assertEquals( "'logout' flow is disabled for 'myidprovider' id provider", ex.getMessage() );
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        this.request.setMethod( HttpMethod.CONNECT );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method CONNECT not allowed", ex.getMessage() );
    }

    @Test
    void testContextPathConfiguration()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final IdProviderKey myIdProvider = IdProviderKey.from( "myidprovider" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/" );
        when( virtualHost.getIdProviders() ).thenReturn( Map.of( myIdProvider, idProvider() ) );
        when( rawRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        this.request.setRawPath( "/_/idprovider/myidprovider/login" );

        WebResponse res = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, res.getStatus() );

        // test invalid context path
        this.request.setRawPath( "/webapp/com.enonic.app.myapp/path/_/idprovider/myidprovider/login" );
        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Not a valid idprovider url pattern", ex.getMessage() );
    }

    public VirtualHost initVirtualHost( final HttpServletRequest rawRequest, final VirtualHost virtualHost )
    {
        Mockito.doAnswer( invocation -> virtualHostKey = invocation.getArgument( 0 ) )
            .when( rawRequest )
            .setAttribute( Mockito.any(), Mockito.isA( VirtualHost.class ) );

        return virtualHost;
    }
    private static VirtualHostIdProvider idProvider( final String... flows )
    {
        return VirtualHostIdProvider.create().flows( Set.of( flows ) ).build();
    }

}
