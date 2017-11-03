package com.enonic.xp.portal.impl.app;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.resource.BytesResource;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceManager;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.impl.exception.ExceptionMapperImpl;

import static org.junit.Assert.*;

public class AppHandlerTest
{
    private AppHandler handler;

    private ResourceService resourceService;

    private ControllerScriptFactory controllerScriptFactory;

    private ExceptionRenderer exceptionRenderer;

    private WebRequest request;

    private WebHandlerChain chain;

    @Before
    public void setup()
    {
        this.resourceService = Mockito.mock( ResourceService.class );
        this.controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        this.exceptionRenderer = Mockito.mock( ExceptionRenderer.class );

        this.handler = new AppHandler();
        this.handler.setResourceService( this.resourceService );
        this.handler.setControllerScriptFactory( this.controllerScriptFactory );
        this.handler.setExceptionMapper( new ExceptionMapperImpl() );
        this.handler.setExceptionRenderer( this.exceptionRenderer );

        this.request = new WebRequest();
        this.request.setRawRequest( Mockito.mock( HttpServletRequest.class ) );
        this.chain = Mockito.mock( WebHandlerChain.class );

        Mockito.when( this.resourceService.getResource( Mockito.any() ) ).thenReturn(
            new BytesResource( ResourceKey.from( "myapp:/unknown.txt" ), null ) );

        Mockito.when( this.exceptionRenderer.render( Mockito.any(), Mockito.any() ) ).thenReturn(
            WebResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build() );

        final TraceManager manager = Mockito.mock( TraceManager.class );
        final Trace trace = Mockito.mock( Trace.class );
        Mockito.when( manager.newTrace( Mockito.any(), Mockito.any() ) ).thenReturn( trace );
        com.enonic.xp.trace.Tracer.setManager( manager );
    }

    @Test
    public void canHandle()
    {
        this.request.setRawPath( "/portal/a/b" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setRawPath( "/app/myapp" );
        assertEquals( true, this.handler.canHandle( this.request ) );

        this.request.setRawPath( "/app/myapp/a/b" );
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void handle_noMatch()
        throws Exception
    {
        final WebResponse response = WebResponse.create().build();
        Mockito.when( this.chain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( response );

        this.request.setRawPath( "/portal/a/b" );
        assertSame( response, this.handler.doHandle( this.request, null, this.chain ) );
    }

    private Resource mockResource( final String uri, final byte[] data )
    {
        final ResourceKey key = ResourceKey.from( uri );
        final Resource resource = new BytesResource( key, data );
        Mockito.when( this.resourceService.getResource( key ) ).thenReturn( resource );
        return resource;
    }

    @Test
    public void handle_serveAsset()
        throws Exception
    {
        final Resource resource = mockResource( "myapp:/assets/a/b.txt", "hello".getBytes() );

        this.request.setRawPath( "/app/myapp/a/b.txt" );

        final WebResponse response = this.handler.doHandle( this.request, null, this.chain );
        assertEquals( HttpStatus.OK, response.getStatus() );
        assertSame( resource, response.getBody() );
    }

    @Test
    public void handle_executeController()
        throws Exception
    {
        mockResource( "myapp:/assets/a.txt", null );

        this.request.setRawPath( "/app/myapp/a.txt" );

        final ControllerScript script = Mockito.mock( ControllerScript.class );
        Mockito.when( this.controllerScriptFactory.fromScript( ResourceKey.from( "myapp:/main.js" ) ) ).thenReturn( script );

        final PortalResponse response = PortalResponse.create().build();
        Mockito.when( script.execute( Mockito.any() ) ).thenReturn( response );

        assertSame( response, this.handler.doHandle( this.request, null, this.chain ) );
    }

    @Test
    public void handle_executeController_error()
        throws Exception
    {
        mockResource( "myapp:/assets/a.txt", null );

        this.request.setRawPath( "/app/myapp/a.txt" );

        final WebResponse response = this.handler.doHandle( this.request, null, this.chain );
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus() );
    }
}

