package com.enonic.xp.portal.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceManager;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.impl.exception.ExceptionMapperImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebAppHandlerTest
{
    private WebAppHandler handler;

    private ControllerScriptFactory controllerScriptFactory;

    private PortalRequest request;

    private WebHandlerChain chain;

    @BeforeEach
    void setup()
    {
        ExceptionRenderer exceptionRenderer = mock( ExceptionRenderer.class );

        this.controllerScriptFactory = mock( ControllerScriptFactory.class );

        this.handler = new WebAppHandler();
        this.handler.setControllerScriptFactory( this.controllerScriptFactory );
        this.handler.setExceptionMapper( new ExceptionMapperImpl() );
        this.handler.setExceptionRenderer( exceptionRenderer );

        this.request = new PortalRequest();
        this.request.setRawRequest( mock( HttpServletRequest.class ) );
        this.chain = mock( WebHandlerChain.class );

        when( exceptionRenderer.render( any(), any() ) ).thenReturn(
            WebResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build() );

        final TraceManager manager = mock( TraceManager.class );
        final Trace trace = mock( Trace.class );
        when( manager.newTrace( any(), any() ) ).thenReturn( trace );
        Tracer.setManager( manager );
    }

    @AfterEach
    void tearDown()
    {
        Tracer.setManager( null );
    }

    @Test
    void canHandle()
    {
        this.request.setRawPath( "/site/a/b" );
        this.request.setBaseUri( "/site" );

        assertFalse( this.handler.canHandle( this.request ) );

        this.request.setRawPath( "/webapp/myapp" );
        this.request.setBaseUri( "/webapp/myapp" );
        assertTrue( this.handler.canHandle( this.request ) );

        this.request.setRawPath( "/webapp/myapp/a/b" );
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    void handle_executeController()
        throws Exception
    {
        this.request.setApplicationKey( ApplicationKey.from( "myapp" ) );
        this.request.setBaseUri( "/webapp/myapp" );
        this.request.setRawPath( "/webapp/myapp/a.txt" );

        final ControllerScript script = mock( ControllerScript.class );
        when( this.controllerScriptFactory.fromScript( ResourceKey.from( "myapp:/webapp/webapp.js" ) ) ).thenReturn( script );

        final PortalResponse response = PortalResponse.create().build();
        when( script.execute( any() ) ).thenReturn( response );

        assertSame( response, this.handler.doHandle( this.request, null, this.chain ) );
        assertEquals( "/webapp/myapp", this.request.getContextPath() );
    }

    @Test
    void handle_executeController_error()
        throws Exception
    {
        this.request.setApplicationKey( ApplicationKey.from( "myapp" ) );
        this.request.setRawPath( "/webapp/myapp/a.txt" );
        this.request.setBaseUri( "/webapp/myapp" );

        final WebResponse response = this.handler.doHandle( this.request, null, this.chain );
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus() );
    }

    @Test
    void handle_noRedirectWhenHasTrailingSlash()
        throws Exception
    {
        this.request.setApplicationKey( ApplicationKey.from( "myapp" ) );
        this.request.setBaseUri( "/webapp/myapp" );
        this.request.setPath( "/webapp/myapp" );
        this.request.setRawPath( "/webapp/myapp/" );

        final ControllerScript script = mock( ControllerScript.class );
        when( this.controllerScriptFactory.fromScript( ResourceKey.from( "myapp:/webapp/webapp.js" ) ) ).thenReturn( script );

        final PortalResponse response = PortalResponse.create().build();
        when( script.execute( any() ) ).thenReturn( response );

        assertSame( response, this.handler.doHandle( this.request, null, this.chain ) );
        assertEquals( "/webapp/myapp", this.request.getContextPath() );
    }

    @Test
    void doHandle_redirectsWhenNoTrailingSlash()
        throws Exception
    {
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );

        final PortalRequest webRequest = new PortalRequest();
        webRequest.setRawPath( "/webapp/myapp" );
        webRequest.setPath( "/webapp/myapp" );
        webRequest.setBaseUri( "/webapp/myapp" );
        webRequest.setRawRequest( rawRequest );

        final WebResponse webResponse = WebResponse.create().build();

        WebResponse response = this.handler.doHandle( webRequest, webResponse, null );

        assertEquals( HttpStatus.TEMPORARY_REDIRECT, response.getStatus() );
        assertEquals( "/webapp/myapp/", response.getHeaders().get( "Location" ) );
    }

    @Test
    void doHandle_redirectsWhenNoTrailingSlashWithQueryString()
        throws Exception
    {
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );
        when( rawRequest.getQueryString() ).thenReturn( "param=value&other=test" );

        final PortalRequest webRequest = new PortalRequest();
        webRequest.setRawPath( "/webapp/myapp" );
        webRequest.setPath( "/webapp/myapp" );
        webRequest.setBaseUri( "/webapp/myapp" );
        webRequest.setRawRequest( rawRequest );

        final WebResponse webResponse = WebResponse.create().build();

        WebResponse response = this.handler.doHandle( webRequest, webResponse, null );

        assertEquals( HttpStatus.TEMPORARY_REDIRECT, response.getStatus() );
        assertEquals( "/webapp/myapp/?param=value&other=test", response.getHeaders().get( "Location" ) );
    }
}

