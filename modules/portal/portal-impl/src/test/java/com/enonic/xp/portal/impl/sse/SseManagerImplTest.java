package com.enonic.xp.portal.impl.sse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.sse.SseEndpoint;
import com.enonic.xp.portal.sse.SseEvent;
import com.enonic.xp.portal.sse.SseEventType;
import com.enonic.xp.web.sse.SseConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SseManagerImplTest
{
    private SseManagerImpl manager;

    @BeforeEach
    void setup()
    {
        ContextBuilder.create().build().runWith( () -> ContextAccessor.current() );
        manager = new SseManagerImpl();
    }

    @Test
    void setupSse()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync( req, res ) ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        when( res.getWriter() ).thenReturn( new PrintWriter( new StringWriter() ) );

        final SseConfig config = new SseConfig();
        config.setData( Map.of( "key", "value" ) );

        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( config );

        ContextBuilder.create().build().runWith( () -> {
            final String id = manager.setupSse( req, res, endpoint );
            assertNotNull( id );

            verify( res ).setContentType( "text/event-stream" );
            verify( res ).setCharacterEncoding( "UTF-8" );
            verify( res ).setHeader( "Cache-Control", "no-cache" );
            verify( res ).setHeader( "Connection", "keep-alive" );

            verify( endpoint ).onEvent( any( SseEvent.class ) );
        } );
    }

    @Test
    void sendToGroup()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync( req, res ) ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        final StringWriter sw = new StringWriter();
        when( res.getWriter() ).thenReturn( new PrintWriter( sw ) );

        final SseConfig config = new SseConfig();
        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( config );

        ContextBuilder.create().build().runWith( () -> {
            final String id = manager.setupSse( req, res, endpoint );
            manager.addToGroup( "testGroup", id );

            assertEquals( 1, manager.getGroupSize( "testGroup" ) );

            manager.sendToGroup( "testGroup", "update", "hello" );

            final String output = sw.toString();
            assertNotNull( output );
        } );
    }

    @Test
    void groupOperations()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync( req, res ) ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        when( res.getWriter() ).thenReturn( new PrintWriter( new StringWriter() ) );

        final SseConfig config = new SseConfig();
        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( config );

        ContextBuilder.create().build().runWith( () -> {
            final String id = manager.setupSse( req, res, endpoint );
            manager.addToGroup( "g1", id );

            assertEquals( 1, manager.getGroupSize( "g1" ) );
            assertEquals( 0, manager.getGroupSize( "g2" ) );

            manager.removeFromGroup( "g1", id );
            assertEquals( 0, manager.getGroupSize( "g1" ) );
        } );
    }

    @Test
    void close()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync( req, res ) ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        when( res.getWriter() ).thenReturn( new PrintWriter( new StringWriter() ) );

        final SseConfig config = new SseConfig();
        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( config );

        ContextBuilder.create().build().runWith( () -> {
            final String id = manager.setupSse( req, res, endpoint );
            manager.close( id );

            verify( asyncContext ).complete();
        } );
    }

    @Test
    void send_noEntry()
    {
        manager.send( "nonexistent", "event", "data", null );
        // should not throw
    }

    @Test
    void close_noEntry()
    {
        manager.close( "nonexistent" );
        // should not throw
    }
}
