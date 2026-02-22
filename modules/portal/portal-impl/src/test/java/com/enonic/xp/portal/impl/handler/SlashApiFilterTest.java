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
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.serializer.WebSerializerService;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

import static org.mockito.ArgumentMatchers.any;
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

    private ExceptionMapper exceptionMapper;

    private ExceptionRenderer exceptionRenderer;

    private WebSerializerService webSerializerService;

    WebSocketContextFactory webSocketContextFactory;

    @BeforeEach
    void setUp()
    {
        slashApiHandler = mock( SlashApiHandler.class );
        exceptionMapper = mock( ExceptionMapper.class );
        exceptionRenderer = mock( ExceptionRenderer.class );
        webSerializerService = mock( WebSerializerService.class );
        webSocketContextFactory = mock();

        filter = new SlashApiFilter( slashApiHandler, exceptionMapper, exceptionRenderer, webSerializerService, webSocketContextFactory );
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
    void handlesExceptionFromHandler()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/com.enonic.app.myapp:myapi" );

        final WebRequest webRequest = new WebRequest();
        when( webSerializerService.request( req ) ).thenReturn( webRequest );

        final RuntimeException cause = new RuntimeException( "test error" );
        when( slashApiHandler.handle( any( WebRequest.class ) ) ).thenThrow( cause );

        final WebException webException = new WebException( HttpStatus.INTERNAL_SERVER_ERROR, "test error" );
        when( exceptionMapper.map( cause ) ).thenReturn( webException );

        final WebResponse errorResponse = WebResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build();
        when( exceptionRenderer.render( eq( webRequest ), eq( webException ) ) ).thenReturn( errorResponse );

        filter.doFilter( req, res, chain );

        verify( exceptionMapper ).map( cause );
        verify( exceptionRenderer ).render( eq( webRequest ), eq( webException ) );
        verify( webSerializerService ).response( eq( webRequest ), eq( errorResponse ), eq( res ) );
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
