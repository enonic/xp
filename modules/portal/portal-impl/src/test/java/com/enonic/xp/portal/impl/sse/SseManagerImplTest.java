package com.enonic.xp.portal.impl.sse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.web.sse.SseEndpoint;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseMessage;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.sse.SseConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        ContextBuilder.create().build().runWith( ContextAccessor::current );
        manager = new SseManagerImpl();
    }

    @Test
    void setupSse()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final WebRequest webReq = new WebRequest();
        webReq.setRawRequest( req );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync() ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        when( res.getWriter() ).thenReturn( new PrintWriter( new StringWriter() ) );

        final SseConfig config = new SseConfig( GenericValue.newObject().put( "key", "value" ).build(), -1, 0 );

        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( config );

        ContextBuilder.create().build().runWith( () -> {
            final UUID id = manager.setupSse( webReq, endpoint );
            assertNotNull( id );

            verify( res ).setContentType( "text/event-stream" );
            verify( res ).setCharacterEncoding( StandardCharsets.UTF_8 );
            verify( res ).setHeader( "Cache-Control", "no-store" );

            verify( endpoint ).onEvent( any( SseEvent.class ) );
        } );
    }

    @Test
    void sendToGroup()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final WebRequest webReq = new WebRequest();
        webReq.setRawRequest( req );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync() ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        final StringWriter sw = new StringWriter();
        when( res.getWriter() ).thenReturn( new PrintWriter( sw ) );

        final SseConfig config = SseConfig.empty();
        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( config );

        ContextBuilder.create().build().runWith( () -> {
            final UUID id = manager.setupSse( webReq, endpoint );
            manager.addToGroup( "testGroup", id );

            assertEquals( 1, manager.getGroupSize( "testGroup" ) );

            manager.sendToGroup( "testGroup", SseMessage.create().event( "update" ).data( "hello" ).build() );

            final String output = sw.toString();
            assertNotNull( output );
        } );
    }

    @Test
    void groupOperations()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final WebRequest webReq = new WebRequest();
        webReq.setRawRequest( req );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync() ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        when( res.getWriter() ).thenReturn( new PrintWriter( new StringWriter() ) );

        final SseConfig config = SseConfig.empty();
        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( config );

        ContextBuilder.create().build().runWith( () -> {
            final UUID id = manager.setupSse( webReq, endpoint );
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
        final WebRequest webReq = new WebRequest();
        webReq.setRawRequest( req );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync() ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        when( res.getWriter() ).thenReturn( new PrintWriter( new StringWriter() ) );

        final SseConfig config = SseConfig.empty();
        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( config );

        ContextBuilder.create().build().runWith( () -> {
            final UUID id = manager.setupSse( webReq, endpoint );
            manager.close( id );

            verify( asyncContext ).complete();
        } );
    }

    @Test
    void setupSse_writesRetry()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final WebRequest webReq = new WebRequest();
        webReq.setRawRequest( req );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync() ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        final StringWriter sw = new StringWriter();
        when( res.getWriter() ).thenReturn( new PrintWriter( sw ) );

        final long thirtyDaysMs = Duration.ofDays( 30 ).toMillis();

        final SseConfig config = new SseConfig( GenericValue.newObject().build(), thirtyDaysMs, 0 );

        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( config );

        ContextBuilder.create().build().runWith( () -> {
            manager.setupSse( webReq, endpoint );
            assertEquals( "retry:" + thirtyDaysMs + "\n\n", sw.toString() );
        } );
    }

    @Test
    void setupSse_negativeRetry_notWritten()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final WebRequest webReq = new WebRequest();
        webReq.setRawRequest( req );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync() ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        final StringWriter sw = new StringWriter();
        when( res.getWriter() ).thenReturn( new PrintWriter( sw ) );

        final SseConfig config = new SseConfig( GenericValue.newObject().build(), -1, 0 );
        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( config );

        ContextBuilder.create().build().runWith( () -> {
            manager.setupSse( webReq, endpoint );
            assertEquals( "", sw.toString() );
        } );
    }

    @Test
    void send_toExistingEntry_writesToWriter()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final WebRequest webReq = new WebRequest();
        webReq.setRawRequest( req );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final AsyncContext asyncContext = mock( AsyncContext.class );
        when( req.startAsync() ).thenReturn( asyncContext );
        when( asyncContext.getResponse() ).thenReturn( res );
        final StringWriter sw = new StringWriter();
        when( res.getWriter() ).thenReturn( new PrintWriter( sw ) );

        final SseEndpoint endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( SseConfig.empty() );

        ContextBuilder.create().build().runWith( () -> {
            final UUID id = manager.setupSse( webReq, endpoint );
            sw.getBuffer().setLength( 0 );

            manager.send( id, SseMessage.create().event( "update" ).data( "hello" ).build() );

            assertEquals( "event:update\ndata:hello\n\n", sw.toString() );
        } );
    }

    @Test
    void send_noEntry()
    {
        manager.send( UUID.randomUUID(), SseMessage.create().event( "event" ).data( "data" ).build() );
        // should not throw
    }

    @Test
    void close_noEntry()
    {
        manager.close( UUID.randomUUID() );
        // should not throw
    }

    @Test
    void addToGroup_noEntry()
    {
        manager.addToGroup( "g", UUID.randomUUID() );
        assertEquals( 0, manager.getGroupSize( "g" ) );
    }

    @Test
    void removeFromGroup_noEntry()
    {
        manager.removeFromGroup( "g", UUID.randomUUID() );
        // should not throw
    }

    @Test
    void isOpen_noEntry()
    {
        assertFalse( manager.isOpen( UUID.randomUUID() ) );
    }
}
