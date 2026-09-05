package com.enonic.xp.portal.impl.handler;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.serializer.WebSerializerService;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SlashApiFilterTest
{
    private SlashApiFilter filter;

    private SlashApiHandler slashApiHandler;

    private WebSerializerService webSerializerService;

    WebSocketContextFactory webSocketContextFactory;

    private ExceptionRenderer exceptionRenderer;

    @BeforeEach
    void setUp()
    {
        slashApiHandler = mock( SlashApiHandler.class );
        webSerializerService = mock( WebSerializerService.class );
        webSocketContextFactory = mock();
        exceptionRenderer = mock( ExceptionRenderer.class );

        filter = new SlashApiFilter( slashApiHandler, webSerializerService, webSocketContextFactory, exceptionRenderer );
    }

    @Test
    void delegatesToSlashApiHandler()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/com.enonic.app.myapp:myapi" );

        final WebRequest webRequest = new WebRequest();
        when( webSerializerService.request( req ) ).thenReturn( webRequest );

        final WebResponse expectedResponse = WebResponse.create().status( HttpStatus.OK ).body( "ok" ).build();
        when( slashApiHandler.handle( any( WebRequest.class ) ) ).thenReturn( expectedResponse );

        filter.doFilter( req, res, chain );

        verify( slashApiHandler ).handle( webRequest );
        verify( webSerializerService ).response( eq( webRequest ), eq( expectedResponse ), eq( res ) );
        verifyNoInteractions( chain );
    }

    @Test
    void delegatesWithTrailingPath()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/com.enonic.app.myapp:myapi/some/path" );

        final WebRequest webRequest = new WebRequest();
        when( webSerializerService.request( req ) ).thenReturn( webRequest );

        final WebResponse expectedResponse = WebResponse.create().status( HttpStatus.OK ).build();
        when( slashApiHandler.handle( any( WebRequest.class ) ) ).thenReturn( expectedResponse );

        filter.doFilter( req, res, chain );

        verify( slashApiHandler ).handle( webRequest );
        verifyNoInteractions( chain );
    }

    @Test
    void rendersHandlerFailure()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/com.enonic.app.myapp:myapi" );

        final WebRequest webRequest = new WebRequest();
        when( webSerializerService.request( req ) ).thenReturn( webRequest );

        final WebException cause = new WebException( HttpStatus.NOT_FOUND, "API not found" );
        when( slashApiHandler.handle( webRequest ) ).thenThrow( cause );

        final WebResponse errorResponse = WebResponse.create().status( HttpStatus.NOT_FOUND ).build();
        when( exceptionRenderer.render( webRequest, cause ) ).thenReturn( errorResponse );

        filter.doFilter( req, res, chain );

        verify( exceptionRenderer ).render( webRequest, cause );
        verify( webSerializerService ).response( webRequest, errorResponse, res );
    }

    @Test
    void rendersRequestSerializationFailure()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/com.enonic.app.myapp:myapi" );

        final WebException cause = new WebException( HttpStatus.METHOD_NOT_ALLOWED, "Method BREW not allowed" );
        when( webSerializerService.request( req ) ).thenThrow( cause );

        final WebResponse errorResponse = WebResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        when( exceptionRenderer.render( any( WebRequest.class ), eq( cause ) ) ).thenReturn( errorResponse );

        filter.doFilter( req, res, chain );

        verify( exceptionRenderer ).render( argThat( webRequest -> webRequest.getRawRequest() == req ), eq( cause ) );
        verify( webSerializerService ).response( argThat( webRequest -> webRequest.getRawRequest() == req ), eq( errorResponse ), eq( res ) );
        verifyNoInteractions( slashApiHandler, chain );
    }

    @Test
    void passesThroughNonApiPath()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/status" );

        filter.doFilter( req, res, chain );

        verify( chain ).doFilter( req, res );
        verifyNoInteractions( slashApiHandler );
    }

    @Test
    void passesThroughNullPathInfo()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( null );

        filter.doFilter( req, res, chain );

        verify( chain ).doFilter( req, res );
        verifyNoInteractions( slashApiHandler );
    }

    @Test
    void webSocketResponseSkipsSerialization()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/com.enonic.app.myapp:myapi" );

        final WebRequest webRequest = new WebRequest();
        webRequest.setWebSocketContext( mock() );
        when( webSerializerService.request( req ) ).thenReturn( webRequest );

        final WebSocketConfig webSocketConfig = mock( WebSocketConfig.class );
        final WebResponse wsResponse = WebResponse.create().status( HttpStatus.OK ).webSocket( webSocketConfig ).build();
        when( slashApiHandler.handle( any( WebRequest.class ) ) ).thenReturn( wsResponse );
        when( webSocketContextFactory.newContext( req, res ) ).thenReturn( mock( WebSocketContext.class ) );

        filter.doFilter( req, res, chain );

        verify( slashApiHandler ).handle( webRequest );
        verify( webSerializerService ).request( req );
        verify( webSerializerService, never() ).response( any(), any(), any() );
    }
}
