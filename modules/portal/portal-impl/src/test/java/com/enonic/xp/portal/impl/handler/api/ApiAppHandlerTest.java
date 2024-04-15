package com.enonic.xp.portal.impl.handler.api;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceManager;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.impl.exception.ExceptionMapperImpl;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiAppHandlerTest
{
    private ApiAppHandler handler;

    private ControllerScriptFactory controllerScriptFactory;

    private ApiDescriptorService apiDescriptorService;

    @BeforeEach
    public void setup()
    {
        this.controllerScriptFactory = mock( ControllerScriptFactory.class );
        this.apiDescriptorService = mock( ApiDescriptorService.class );

        ExceptionRenderer exceptionRenderer = mock( ExceptionRenderer.class );
        ExceptionMapper exceptionMapper = new ExceptionMapperImpl();

        this.handler = new ApiAppHandler( controllerScriptFactory, apiDescriptorService, exceptionMapper, exceptionRenderer );

        when( exceptionRenderer.render( Mockito.any(), Mockito.any() ) ).thenReturn(
            WebResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build() );
    }

    @AfterEach
    public void tearDown()
    {
        Tracer.setManager( null );
    }

    @Test
    public void testCanHandle()
    {
        WebRequest webRequest = mock( WebRequest.class );

        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp/api-key" );
        assertTrue( this.handler.canHandle( webRequest ) );

        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp" );
        assertTrue( this.handler.canHandle( webRequest ) );

        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp/api-key/" );
        assertTrue( this.handler.canHandle( webRequest ) );

        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp/api-key/contentPath" );
        assertTrue( this.handler.canHandle( webRequest ) );

        when( webRequest.getRawPath() ).thenReturn( "/path" );
        assertFalse( this.handler.canHandle( webRequest ) );

        when( webRequest.getRawPath() ).thenReturn( "/admin/api/com.enonic.app.myapp/api-key" );
        assertFalse( this.handler.canHandle( webRequest ) );

        when( webRequest.getRawPath() ).thenReturn( "/adm/api/com.enonic.app.myapp/api-key" );
        assertFalse( this.handler.canHandle( webRequest ) );

        when( webRequest.getEndpointPath() ).thenReturn( "/_/com.enonic.app.myapp/api-key" );
        when( webRequest.getRawPath() ).thenReturn( "/site/project/branch/content-path/_/com.enonic.app.myapp/api-key" );
        assertTrue( this.handler.canHandle( webRequest ) );

        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/api/media/api-key" );
        assertFalse( this.handler.canHandle( webRequest ) );
    }

    @Test
    public void testDoHandleCheckAccess()
    {
        final PortalRequest request = new PortalRequest();
        request.setRawRequest( mock( HttpServletRequest.class ) );
        request.setRawPath( "/api/com.enonic.app.myapp/api/" );

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( null );

        WebException ex = assertThrows( WebException.class, () -> this.handler.doHandle( request, null, null ) );
        assertEquals( "API [com.enonic.app.myapp:api] not found", ex.getMessage() );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api" ) )
            .allowedPrincipals( PrincipalKeys.from( RoleKeys.CONTENT_MANAGER_ADMIN ) )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        ex = assertThrows( WebException.class, () -> this.handler.doHandle( request, null, null ) );
        assertEquals( "You don't have permission to access \"api\" API for \"com.enonic.app.myapp\"", ex.getMessage() );
    }

    @Test
    public void testDoHandle()
    {
        WebSocketContext webSocketContext = mock( WebSocketContext.class );

        final PortalRequest request = new PortalRequest();
        request.setRawRequest( mock( HttpServletRequest.class ) );
        request.setRawPath( "/api/com.enonic.app.myapp" );
        request.setWebSocketContext( webSocketContext );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api" ) )
            .allowedPrincipals( PrincipalKeys.from( RoleKeys.CONTENT_MANAGER_ADMIN, RoleKeys.ADMIN ) )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        final ControllerScript script = mock( ControllerScript.class );
        when( this.controllerScriptFactory.fromScript( ResourceKey.from( "com.enonic.app.myapp:/apis/api.js" ) ) ).thenReturn( script );

        final WebSocketConfig webSocketConfig = mock( WebSocketConfig.class );

        final PortalResponse response = PortalResponse.create().webSocket( webSocketConfig ).build();
        when( script.execute( Mockito.any() ) ).thenReturn( response );

        final TraceManager manager = mock( TraceManager.class );
        final Trace trace = mock( Trace.class );
        when( manager.newTrace( Mockito.any(), Mockito.any() ) ).thenReturn( trace );
        Tracer.setManager( manager );

        WebResponse webResponse = ContextBuilder.create()
            .repositoryId( "default" )
            .branch( "draft" )
            .authInfo( AuthenticationInfo.create().user( User.ANONYMOUS ).principals( RoleKeys.ADMIN ).build() )
            .build()
            .callWith( () -> this.handler.doHandle( request, WebResponse.create().build(), null ) );

        assertEquals( response, webResponse );
    }

    @Test
    public void testDoHandleThrowError()
    {
        final PortalRequest request = new PortalRequest();
        request.setRawRequest( mock( HttpServletRequest.class ) );
        request.setRawPath( "/api/com.enonic.app.myapp/api" );

        ApiDescriptor apiDescriptor = ApiDescriptor.create()
            .key( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.myapp" ), "api" ) )
            .allowedPrincipals( PrincipalKeys.from( RoleKeys.CONTENT_MANAGER_ADMIN, RoleKeys.ADMIN ) )
            .build();

        when( apiDescriptorService.getByKey( any( DescriptorKey.class ) ) ).thenReturn( apiDescriptor );

        final ControllerScript script = mock( ControllerScript.class );

        when( this.controllerScriptFactory.fromScript( ResourceKey.from( "com.enonic.app.myapp:/apis/api.js" ) ) ).thenReturn( script );
        when( script.execute( Mockito.any() ) ).thenThrow( RuntimeException.class );

        WebResponse webResponse = ContextBuilder.create()
            .repositoryId( "default" )
            .branch( "draft" )
            .authInfo( AuthenticationInfo.create().user( User.ANONYMOUS ).principals( RoleKeys.ADMIN ).build() )
            .build()
            .callWith( () -> this.handler.doHandle( request, WebResponse.create().build(), null ) );

        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, webResponse.getStatus() );
    }
}
