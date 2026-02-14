package com.enonic.xp.portal.impl.handler;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.serializer.RequestSerializerService;
import com.enonic.xp.web.serializer.ResponseSerializationService;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SlashApiServletTest
{
    private SlashApiServlet filter;

    private SlashApiHandler slashApiHandler;

    private ExceptionMapper exceptionMapper;

    private ExceptionRenderer exceptionRenderer;

    private WebSocketContextFactory webSocketContextFactory;

    private ResponseSerializationService responseSerializationService;

    private RequestSerializerService requestSerializerService;

    @BeforeEach
    void setUp()
    {
        slashApiHandler = mock( SlashApiHandler.class );
        exceptionMapper = mock( ExceptionMapper.class );
        exceptionRenderer = mock( ExceptionRenderer.class );
        webSocketContextFactory = mock( WebSocketContextFactory.class );
        responseSerializationService = mock( ResponseSerializationService.class );
        requestSerializerService = mock( RequestSerializerService.class );

        filter = new SlashApiServlet( slashApiHandler, exceptionMapper, exceptionRenderer, webSocketContextFactory,
                                      responseSerializationService, requestSerializerService );
    }

    @Test
    void delegatesToSlashApiHandler()
        throws ServletException, IOException, Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/com.enonic.app.myapp:myapi" );

        final WebRequest webRequest = new WebRequest();
        when( requestSerializerService.serialize( req ) ).thenReturn( webRequest );

        final WebResponse expectedResponse = WebResponse.create().status( HttpStatus.OK ).body( "ok" ).build();
        when( slashApiHandler.handle( any( WebRequest.class ) ) ).thenReturn( expectedResponse );
        when( webSocketContextFactory.newContext( req, res ) ).thenReturn( null );

        filter.doFilter( req, res, chain );

        verify( slashApiHandler ).handle( webRequest );
        verify( responseSerializationService ).serialize( eq( webRequest ), eq( expectedResponse ), eq( res ) );
        verifyNoInteractions( chain );
    }

    @Test
    void delegatesWithTrailingPath()
        throws ServletException, IOException, Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/com.enonic.app.myapp:myapi/some/path" );

        final WebRequest webRequest = new WebRequest();
        when( requestSerializerService.serialize( req ) ).thenReturn( webRequest );

        final WebResponse expectedResponse = WebResponse.create().status( HttpStatus.OK ).build();
        when( slashApiHandler.handle( any( WebRequest.class ) ) ).thenReturn( expectedResponse );
        when( webSocketContextFactory.newContext( req, res ) ).thenReturn( null );

        filter.doFilter( req, res, chain );

        verify( slashApiHandler ).handle( webRequest );
        verifyNoInteractions( chain );
    }

    @Test
    void passesThroughNonApiPath()
        throws ServletException, IOException
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/status" );

        filter.doFilter( req, res, chain );

        chain.doFilter( req, res );
        verifyNoInteractions( slashApiHandler );
    }

    @Test
    void passesThroughNullPathInfo()
        throws ServletException, IOException
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( null );

        filter.doFilter( req, res, chain );

        chain.doFilter( req, res );
        verifyNoInteractions( slashApiHandler );
    }

    @Test
    void handlesExceptionFromHandler()
        throws ServletException, IOException, Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/com.enonic.app.myapp:myapi" );

        final WebRequest webRequest = new WebRequest();
        when( requestSerializerService.serialize( req ) ).thenReturn( webRequest );

        final RuntimeException cause = new RuntimeException( "test error" );
        when( slashApiHandler.handle( any( WebRequest.class ) ) ).thenThrow( cause );

        final WebException webException = new WebException( HttpStatus.INTERNAL_SERVER_ERROR, "test error" );
        when( exceptionMapper.map( cause ) ).thenReturn( webException );

        final WebResponse errorResponse = WebResponse.create().status( HttpStatus.INTERNAL_SERVER_ERROR ).build();
        when( exceptionRenderer.render( eq( webRequest ), eq( webException ) ) ).thenReturn( errorResponse );
        when( webSocketContextFactory.newContext( req, res ) ).thenReturn( null );

        filter.doFilter( req, res, chain );

        verify( exceptionMapper ).map( cause );
        verify( exceptionRenderer ).render( eq( webRequest ), eq( webException ) );
        verify( responseSerializationService ).serialize( eq( webRequest ), eq( errorResponse ), eq( res ) );
    }

    @Test
    void webSocketResponseSkipsSerialization()
        throws ServletException, IOException, Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getPathInfo() ).thenReturn( "/com.enonic.app.myapp:myapi" );

        final WebRequest webRequest = new WebRequest();
        when( requestSerializerService.serialize( req ) ).thenReturn( webRequest );

        final WebSocketContext webSocketContext = mock( WebSocketContext.class );
        when( webSocketContextFactory.newContext( req, res ) ).thenReturn( webSocketContext );

        final com.enonic.xp.web.websocket.WebSocketConfig webSocketConfig = mock( com.enonic.xp.web.websocket.WebSocketConfig.class );
        final WebResponse wsResponse = WebResponse.create().status( HttpStatus.OK ).webSocket( webSocketConfig ).build();
        when( slashApiHandler.handle( any( WebRequest.class ) ) ).thenReturn( wsResponse );

        filter.doFilter( req, res, chain );

        verify( slashApiHandler ).handle( webRequest );
        verifyNoInteractions( responseSerializationService );
    }
}
