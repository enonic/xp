package com.enonic.xp.portal.impl.handler.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceManager;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiIdentityHandlerTest
    extends BaseHandlerTest
{
    private ApiIdentityHandler handler;

    @BeforeEach
    public void setUp()
        throws IOException
    {
        IdProviderControllerService idProviderControllerService = mock( IdProviderControllerService.class );
        this.handler = new ApiIdentityHandler( idProviderControllerService, mock( RedirectChecksumService.class ) );

        when( idProviderControllerService.execute( any() ) ).thenAnswer( invocation -> {
            Object[] args = invocation.getArguments();
            final IdProviderControllerExecutionParams arg = (IdProviderControllerExecutionParams) args[0];
            if ( IdProviderKey.from( "myidprovider" ).equals( arg.getIdProviderKey() ) &&
                ( "get".equals( arg.getFunctionName() ) || "login".equals( arg.getFunctionName() ) ||
                    "logout".equals( arg.getFunctionName() ) ) )
            {
                return PortalResponse.create().build();
            }
            return null;
        } );
    }

    @Test
    void testCanHandle()
    {
        PortalRequest request = new PortalRequest();
        request.setMethod( HttpMethod.GET );

        request.setRawPath( "/api/idprovider/system/_static/css/main.css" );
        assertTrue( handler.canHandle( request ) );

        request.setRawPath( "/admin/api/idprovider/system/_static/css/main.css" );
        assertFalse( handler.canHandle( request ) );

        request.setRawPath( "/adm/api/idprovider/system/_static/css/main.css" );
        assertFalse( handler.canHandle( request ) );

        request.setRawPath( "/api/idprovider/system/logout" );
        assertTrue( handler.canHandle( request ) );

        request.setRawPath( "/api/idprovider/system/login" );
        assertTrue( handler.canHandle( request ) );

        request.setRawPath( "/api/idprovider/system/login?redirect=url&_ticket=hash" );
        assertTrue( handler.canHandle( request ) );

        request.setRawPath( "/api/idprovider/system/logout?redirect=url&_ticket=hash" );
        assertTrue( handler.canHandle( request ) );
    }

    @Test
    public void testOrder()
    {
        assertEquals( 0, handler.getOrder() );
    }

    @Test
    public void testHandleWithVirtualHostNotEnabled()
    {
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider" ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        PortalRequest request = new PortalRequest();
        request.setRawRequest( rawRequest );
        request.setMethod( HttpMethod.GET );
        request.setRawPath( "/api/idprovider/myidprovider/_static/css/main.css" );

        WebException e = assertThrows( WebException.class, () -> this.handler.handle( request, PortalResponse.create().build(), null ) );
        assertEquals( "'myidprovider' id provider is forbidden", e.getMessage() );
    }

    @Test
    public void testHandleWithEnabledVirtualHost()
        throws Exception
    {
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider", "myidprovider" ) );
        when( virtualHost.getDefaultIdProviderKey() ).thenReturn( IdProviderKey.from( "myidprovider" ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        PortalRequest request = new PortalRequest();
        request.setRawRequest( rawRequest );
        request.setMethod( HttpMethod.GET );
        request.setRawPath( "/api/idprovider/myidprovider/_static/css/main.css" );

        // Test without trace
        WebResponse portalResponse = this.handler.handle( request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );

        // Test with trace
        TraceManager manager = mock( TraceManager.class );
        Trace trace = mock( Trace.class );
        when( manager.newTrace( any(), any() ) ).thenReturn( trace );
        Tracer.setManager( manager );

        try
        {
            portalResponse = this.handler.handle( request, PortalResponse.create().build(), null );
            assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        }
        finally
        {
            Tracer.setManager( null );
        }

        request.setRawPath( "/api/idprovider/myidprovider/login" );

        portalResponse = this.handler.handle( request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
    }

    @Test
    public void testHandleUnknownMethod()
    {
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider", "myidprovider" ) );
        when( virtualHost.getDefaultIdProviderKey() ).thenReturn( IdProviderKey.from( "myidprovider" ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        PortalRequest request = new PortalRequest();
        request.setRawRequest( rawRequest );
        request.setMethod( HttpMethod.DELETE );
        request.setRawPath( "/api/idprovider/myidprovider" );

        WebException e = assertThrows( WebException.class, () -> this.handler.handle( request, PortalResponse.create().build(), null ) );
        assertEquals( "ID Provider function [delete] not found for id provider [myidprovider]", e.getMessage() );
    }

    @Test
    public void testHandleLogout()
    {
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider", "myidprovider" ) );
        when( virtualHost.getDefaultIdProviderKey() ).thenReturn( IdProviderKey.from( "myidprovider" ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        PortalRequest request = mock( PortalRequest.class );
        when( request.getRawRequest() ).thenReturn( rawRequest );
        when( request.getRawPath() ).thenReturn(
            "/api/idprovider/myidprovider/logout?redirect=url&_ticket=feafa6dc0d458fd01abee1cbfca8c6605a4a348e" );
        when( request.getMethod() ).thenReturn( HttpMethod.GET );

        Multimap<String, String> params = HashMultimap.create();
        params.put( "redirect", "url" );
        params.put( "_ticket", "feafa6dc0d458fd01abee1cbfca8c6605a4a348e" );

        when( request.getParams() ).thenReturn( params );

        Context context = ContextBuilder.create().build();
        context.getLocalScope().setSession( new SessionMock() );

        context.runWith( () -> {
            try
            {
                WebResponse portalResponse = this.handler.handle( request, PortalResponse.create().build(), null );
                assertEquals( HttpStatus.OK, portalResponse.getStatus() );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }

            // without _ticket
            params.removeAll( "_ticket" );
            WebException e =
                assertThrows( WebException.class, () -> this.handler.handle( request, PortalResponse.create().build(), null ) );
            assertEquals( "Missing ticket parameter", e.getMessage() );
        } );
    }

    private VirtualHost initVirtualHost( final HttpServletRequest rawRequest, final VirtualHost virtualHost )
    {
        when( rawRequest.getAttribute( isA( String.class ) ) ).thenAnswer(
            ( InvocationOnMock invocation ) -> VirtualHost.class.getName().equals( invocation.getArguments()[0] )
                ? virtualHost
                : generateDefaultVirtualHost() );

        return virtualHost;
    }

    private VirtualHost generateDefaultVirtualHost()
    {
        VirtualHost result = mock( VirtualHost.class );

        when( result.getHost() ).thenReturn( "host" );
        when( result.getSource() ).thenReturn( "/" );
        when( result.getTarget() ).thenReturn( "/" );
        when( result.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( IdProviderKey.system() ) );

        return result;
    }
}
