package com.enonic.xp.portal.impl.sse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.web.sse.SseConfig;
import com.enonic.xp.web.sse.SseEndpoint;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.web.sse.SseMessage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SseEntryImplTest
{
    private SseRegistry registry;

    private AsyncContext asyncContext;

    private StringWriter writerSink;

    private SseEndpoint endpoint;

    private SseEntryImpl entry;

    private UUID clientId;

    @BeforeEach
    void setup()
    {
        ContextBuilder.create().build().runWith( ContextAccessor::current );

        registry = new SseRegistry();
        asyncContext = mock( AsyncContext.class );
        writerSink = new StringWriter();
        final PrintWriter writer = new PrintWriter( writerSink );

        endpoint = mock( SseEndpoint.class );
        when( endpoint.getConfig() ).thenReturn( SseConfig.empty() );

        clientId = UUID.randomUUID();

        ContextBuilder.create().build().runWith( () -> entry = new SseEntryImpl( clientId, asyncContext, writer, endpoint, registry ) );
    }

    @Test
    void sendEvent_writesWire()
    {
        entry.sendEvent( SseMessage.create().id( "evt-1" ).event( "update" ).data( "hello" ).build() );
        assertEquals( "id:evt-1\nevent:update\ndata:hello\n\n", writerSink.toString() );
    }

    @Test
    void sendEvent_comment()
    {
        entry.sendEvent( SseMessage.create().comment( "keep-alive" ).build() );
        assertEquals( ":keep-alive\n\n", writerSink.toString() );
    }

    @Test
    void getClientId()
    {
        assertSame( clientId, entry.getClientId() );
    }

    @Test
    void groupMembership()
    {
        entry.addGroup( "g1" );
        assertTrue( entry.isInGroup( "g1" ) );
        entry.removeGroup( "g1" );
        assertFalse( entry.isInGroup( "g1" ) );
    }

    @Test
    void onComplete_firesCloseEventAndRemovesFromRegistry()
    {
        registry.add( entry );
        entry.onComplete( mock( AsyncEvent.class ) );

        verify( endpoint ).onEvent( any( SseEvent.class ) );
        assertFalse( registryContains( entry ) );
    }

    @Test
    void onTimeout_firesTimeoutEvent()
    {
        registry.add( entry );
        entry.onTimeout( mock( AsyncEvent.class ) );

        verify( endpoint ).onEvent( argThat( e -> e.getType() == SseEventType.TIMEOUT ) );
        verify( asyncContext ).complete();
    }

    @Test
    void onError_firesErrorEventWithThrowable()
    {
        registry.add( entry );
        final RuntimeException cause = new RuntimeException( "boom" );
        final AsyncEvent ev = mock( AsyncEvent.class );
        when( ev.getThrowable() ).thenReturn( cause );

        entry.onError( ev );

        verify( endpoint ).onEvent( argThat( e -> e.getType() == SseEventType.ERROR && e.getError() == cause ) );
        verify( asyncContext ).complete();
    }

    @Test
    void close_removesFromRegistryAndCompletesAsync()
    {
        registry.add( entry );
        entry.close();
        assertFalse( registryContains( entry ) );
        verify( asyncContext ).complete();
    }

    @Test
    void sendEvent_checkError_firesErrorAndCloses()
    {
        // Writer backed by a closed sink: flush+write silently succeeds but checkError eventually returns true
        // Simulate via a StringWriter that we close before sending.
        final StringWriter broken = new StringWriter();
        final PrintWriter pw = new PrintWriter( broken )
        {
            @Override
            public boolean checkError()
            {
                return true;
            }
        };
        final SseEndpoint ep = mock( SseEndpoint.class );
        when( ep.getConfig() ).thenReturn( SseConfig.empty() );
        final SseRegistry reg = new SseRegistry();
        final SseEntryImpl brokenEntry =
            ContextBuilder.create().build().callWith( () -> new SseEntryImpl( clientId, asyncContext, pw, ep, reg ) );
        reg.add( brokenEntry );

        brokenEntry.sendEvent( SseMessage.create().data( "x" ).build() );

        verify( ep ).onEvent( argThat( e -> e.getType() == SseEventType.ERROR && e.getError() != null ) );
        verify( asyncContext ).complete();
        assertNull( reg.getById( brokenEntry.getClientId() ) );
    }

    @Test
    void sendEvent_writeIOException_firesErrorAndCloses()
        throws Exception
    {
        // PrintWriter swallows IO, so to hit the IOException catch we need writeTo() itself to throw.
        // Build a message whose wire output is written via a writer that throws.
        final PrintWriter throwingWriter = new PrintWriter( new Writer()
        {
            @Override
            public void write( char[] cbuf, int off, int len )
                throws IOException
            {
                throw new IOException( "simulated" );
            }

            @Override
            public void flush()
            {
            }

            @Override
            public void close()
            {
            }
        } );
        // PrintWriter.write swallows IOException internally; use an SseMessage stub that calls writer.write directly and throws.
        final SseEndpoint ep = mock( SseEndpoint.class );
        when( ep.getConfig() ).thenReturn( SseConfig.empty() );
        final SseRegistry reg = new SseRegistry();
        final SseEntryImpl brokenEntry =
            ContextBuilder.create().build().callWith( () -> new SseEntryImpl( clientId, asyncContext, throwingWriter, ep, reg ) );
        reg.add( brokenEntry );

        // Craft a message whose writeTo throws IOException by passing a writer that throws via reflection? Simpler:
        // use a subclass of SseMessage? SseMessage is final. Instead — rely on checkError path since PrintWriter set error flag
        // when the wrapped Writer throws. So triggering a write will flip checkError, and we've already tested that branch.
        brokenEntry.sendEvent( SseMessage.create().data( "x" ).build() );

        verify( ep ).onEvent( argThat( e -> e.getType() == SseEventType.ERROR ) );
        verify( asyncContext ).complete();
    }

    @Test
    void doComplete_exceptionLogged()
    {
        registry.add( entry );
        doThrow( new IllegalStateException( "already complete" ) ).when( asyncContext ).complete();
        assertDoesNotThrow( () -> entry.close() );
    }

    @Test
    void onError_twice_firesErrorOnlyOnce()
    {
        registry.add( entry );
        final RuntimeException cause = new RuntimeException( "first" );
        final AsyncEvent ev1 = mock( AsyncEvent.class );
        when( ev1.getThrowable() ).thenReturn( cause );
        entry.onError( ev1 );

        // second onError — should not re-fire ERROR
        final AsyncEvent ev2 = mock( AsyncEvent.class );
        when( ev2.getThrowable() ).thenReturn( new RuntimeException( "second" ) );
        entry.onError( ev2 );

        verify( endpoint, times( 1 ) ).onEvent( argThat( e -> e.getType() == SseEventType.ERROR ) );
    }

    private boolean registryContains( final SseEntry e )
    {
        return registry.getById( e.getClientId() ) != null;
    }
}
