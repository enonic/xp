package com.enonic.xp.portal.impl.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IdentityHandlerTest
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
            if ( myIdProvider.equals( arg.getIdProviderKey() ) && "get".equals( arg.getFunctionName() ) )
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
        this.request.setEndpointPath( "/_/idprovider/myidprovider?param1=value1" );
        this.request.setRawPath( "/site/project/branch/_/idprovider/myidprovider?param1=value1" );
        this.request.setRawRequest( rawRequest );

        final VirtualHost virtualHost = mock( VirtualHost.class );

        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/site/project/branch" );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( myIdProvider ) );
        when( virtualHost.getDefaultIdProviderKey() ).thenReturn( myIdProvider );
        when( rawRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        final IdProviderControllerService idProviderControllerService = mock( IdProviderControllerService.class );
        final PortalResponse response = PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        when( idProviderControllerService.execute( Mockito.any() ) ).thenReturn( response );
        this.handler = new IdentityHandler( idProviderControllerService, mock() );

        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build() );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE,PATCH", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/idprovider/" );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build() );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid idprovider url pattern", e.getMessage() );
        }
    }

    @Test
    public void testHandle()
        throws Exception
    {
        final WebResponse portalResponse = this.handler.handle( this.request, PortalResponse.create().build() );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "/site/project/branch/_/idprovider/myidprovider", this.request.getContextPath() );
    }

    @Test
    public void testHandle_redirect()
        throws Exception
    {
        this.request.setEndpointPath( "/_/idprovider/myidprovider/login" );
        this.request.setRawPath( "/site/project/branch/_/idprovider/myidprovider/login" );
        when( redirectChecksumService.verifyChecksum( "https://example.com", "some-good-checksum" ) ).thenReturn( true );

        this.request.getParams().put( "redirect", "https://example.com" );
        this.request.getParams().put( "_ticket", "some-good-checksum" );
        this.handler.handle( this.request, PortalResponse.create().build() );

        assertTrue( this.request.isValidTicket() );
    }

    @Test
    public void testHandle_redirect_invalid()
        throws Exception
    {
        this.request.setEndpointPath( "/_/idprovider/myidprovider/login" );
        this.request.setRawPath( "/site/project/branch/_/idprovider/myidprovider/login" );
        when( redirectChecksumService.verifyChecksum( "https://example.com", "some-bad-checksum" ) ).thenReturn( false );

        this.request.getParams().put( "redirect", "https://example.com" );
        this.request.getParams().put( "_ticket", "some-bad-checksum" );
        this.handler.handle( this.request, PortalResponse.create().build() );

        assertFalse( this.request.isValidTicket() );
    }

    @Test
    public void testHandleWithVirtualHostNotEnabled()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider" ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build() );
        }
        catch ( final WebException e )
        {
            assertEquals( "'myidprovider' id provider is forbidden", e.getMessage() );
        }
    }

    @Test
    public void testHandleWithVirtualHostEnabled()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider", "myidprovider" ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        final WebResponse portalResponse = this.handler.handle( this.request, PortalResponse.create().build() );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "/site/project/branch/_/idprovider/myidprovider", this.request.getContextPath() );
    }

    @Test
    public void testHandleWithEmptyVirtualHostIdProviderConfig()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.empty() );

        VirtualHostHelper.setVirtualHost( rawRequest, virtualHost );

        final WebResponse portalResponse = this.handler.handle( this.request, PortalResponse.create().build() );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "/site/project/branch/_/idprovider/myidprovider", this.request.getContextPath() );
    }

    @Test
    public void testHandleMethodNotAllowed()
    {
        this.request.setMethod( HttpMethod.CONNECT );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request, WebResponse.create().build() ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method CONNECT not allowed", ex.getMessage() );
    }

    @Test
    public void testContextPathConfiguration()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final IdProviderKey myIdProvider = IdProviderKey.from( "myidprovider" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/" );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( myIdProvider ) );
        when( virtualHost.getDefaultIdProviderKey() ).thenReturn( myIdProvider );
        when( rawRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        this.request.setEndpointPath( "/_/idprovider/myidprovider/login" );
        this.request.setRawPath( "/_/idprovider/myidprovider/login" );

        WebResponse res = this.handler.handle( this.request, PortalResponse.create().build() );
        assertEquals( HttpStatus.OK, res.getStatus() );

        // test invalid context path
        this.request.setRawPath( "/webapp/com.enonic.app.myapp/path/_/idprovider/myidprovider/login" );
        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request, PortalResponse.create().build() ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Not a valid idprovider url pattern", ex.getMessage() );
    }

    public VirtualHost initVirtualHost( final HttpServletRequest rawRequest, final VirtualHost virtualHost )
    {
        Mockito.doAnswer( ( InvocationOnMock invocation ) -> virtualHostKey = (String) invocation.getArguments()[0] )
            .when( rawRequest )
            .setAttribute( Mockito.any(), Mockito.isA( VirtualHost.class ) );

        return virtualHost;
    }
}
