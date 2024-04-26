package com.enonic.xp.portal.impl.handler;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceManager;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.impl.exception.ExceptionMapperImpl;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SlashApiHandlerTest
{
    private SlashApiHandler handler;

    private ControllerScriptFactory controllerScriptFactory;

    private ApiDescriptorService apiDescriptorService;

    private ExceptionRenderer exceptionRenderer;

    @BeforeEach
    public void setUp()
    {
        controllerScriptFactory = mock( ControllerScriptFactory.class );
        apiDescriptorService = mock( ApiDescriptorService.class );
        exceptionRenderer = mock( ExceptionRenderer.class );

        handler = new SlashApiHandler( controllerScriptFactory, apiDescriptorService, new ExceptionMapperImpl(), exceptionRenderer );

        when( this.exceptionRenderer.render( any(), any() ) ).thenReturn(
            WebResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build() );

        final TraceManager manager = mock( TraceManager.class );
        final Trace trace = mock( Trace.class );
        when( manager.newTrace( any(), any() ) ).thenReturn( trace );
        Tracer.setManager( manager );
    }

    @AfterEach
    public void tearDown()
    {
        Tracer.setManager( null );
    }

    @Test
    public void testReservedAppKeys()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );

        List.of( "attachment", "image", "error", "idprovider", "service", "asset", "component", "widgets", "media" ).forEach( appKey -> {
            // as endpoint
            when( webRequest.getEndpointPath() ).thenReturn( "/_/" + appKey + "/path" );
            when( webRequest.getRawPath() ).thenReturn( "/path/_/" + appKey + "/path" );

            WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
            assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
            assertEquals( "Application key [" + appKey + "] is reserved", ex.getMessage() );

            // as API
            when( webRequest.getEndpointPath() ).thenReturn( null );
            when( webRequest.getRawPath() ).thenReturn( "/api/" + appKey + "/apiKey/path" );

            ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
            assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
            assertEquals( "Application key [" + appKey + "] is reserved", ex.getMessage() );
        } );
    }

    @Test
    public void testPattern()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );

        final String path = "/site/project/branch/_/com.enonic.app.myapp/api-key";
        when( webRequest.getEndpointPath() ).thenReturn( path );

        IllegalStateException ex = assertThrows( IllegalStateException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( "Invalid API path: " + path, ex.getMessage() );
    }

    @Test
    public void testHttpOptions()
        throws Exception
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.OPTIONS );

        when( webRequest.getEndpointPath() ).thenReturn( "/_/com.enonic.app.myapp/api-key" );

        final WebResponse res = this.handler.handle( webRequest );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testHandleApiNotFound()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp/api-key" );

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( null );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "API [com.enonic.app.myapp:api-key] not found", ex.getMessage() );
    }

    @Test
    public void testHandleApiAccessDenied()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp/api-key" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .allowedPrincipals( PrincipalKeys.create().add( PrincipalKey.from( "role:principalKey" ) ).build() )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( webRequest ) );
        assertEquals( HttpStatus.UNAUTHORIZED, ex.getStatus() );
        assertEquals( "You don't have permission to access \"api-key\" API for \"com.enonic.app.myapp\"", ex.getMessage() );
    }

    @Test
    public void testHandleApi()
        throws Exception
    {
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );

        final WebSocketConfig webSocketConfig = mock( WebSocketConfig.class );

        final PortalResponse portalResponse = PortalResponse.create().webSocket( webSocketConfig ).build();

        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScript.execute( any( PortalRequest.class ) ) ).thenReturn( portalResponse );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenReturn( controllerScript );

        final WebSocketContext webSocketContext = mock( WebSocketContext.class );
        when( webSocketContext.apply( any( WebSocketEndpoint.class ) ) ).thenReturn( true );

        final PortalRequest request = new PortalRequest();
        request.setMethod( HttpMethod.GET );
        request.setEndpointPath( null );
        request.setWebSocketContext( webSocketContext );
        request.setRawPath( "/api/com.enonic.app.myapp/api-key" );
        request.setRawRequest( rawRequest );

        ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( DescriptorKey.from( ApplicationKey.from( "myapp" ), "myapi" ) ).allowedPrincipals( null ).build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );
    }

    @Test
    public void testHandleApiWithoutTracer()
        throws Exception
    {
        Tracer.setManager( null );
        testHandleApi();
    }

    @Test
    public void testHandleApiError()
        throws Exception
    {
        when( this.exceptionRenderer.render( any(), any() ) ).thenReturn(
            WebResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build() );

        when( controllerScriptFactory.fromScript( any( ResourceKey.class ) ) ).thenThrow( new NullPointerException() );

        final PortalRequest request = new PortalRequest();
        request.setMethod( HttpMethod.GET );
        request.setEndpointPath( null );
        request.setRawPath( "/api/com.enonic.app.myapp/api-key" );
        request.setRawRequest( mock( HttpServletRequest.class ) );

        ApiDescriptor apiDescriptor =
            ApiDescriptor.create().key( DescriptorKey.from( ApplicationKey.from( "myapp" ), "myapi" ) ).allowedPrincipals( null ).build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        WebResponse webResponse = this.handler.handle( request );
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, webResponse.getStatus() );
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        final PortalRequest request = new PortalRequest();
        request.setMethod( HttpMethod.CONNECT );
        request.setEndpointPath( null );
        request.setRawPath( "/api/com.enonic.app.myapp/api-key" );
        request.setRawRequest( mock( HttpServletRequest.class ) );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method CONNECT not allowed", ex.getMessage() );
    }

}
