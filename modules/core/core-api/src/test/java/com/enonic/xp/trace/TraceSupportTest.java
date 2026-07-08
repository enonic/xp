package com.enonic.xp.trace;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceSupportTest
{
    @Mock
    TraceManager manager;

    @Mock
    Trace trace;

    @BeforeEach
    void setUp()
    {
        Tracer.setManager( this.manager );
    }

    @AfterEach
    void tearDown()
    {
        Tracer.setManager( null );
    }

    @Test
    void disabledPassesThrough()
        throws Throwable
    {
        Tracer.setManager( null );

        final Object result = new Object();
        assertSame( result, TraceSupport.trace( "test", () -> result ) );

        final AtomicReference<Object> called = new AtomicReference<>();
        TraceSupport.trace( "test", () -> called.set( "called" ) );
        assertEquals( "called", called.get() );

        verifyNoInteractions( this.manager );
    }

    @Test
    void enabledTracesCall()
        throws Throwable
    {
        when( this.manager.newTrace( eq( "myTrace" ), any() ) ).thenReturn( this.trace );

        final Object result = new Object();
        final Object returned = TraceSupport.trace( "myTrace", () -> {
            assertSame( TraceSupportTest.this.trace, Tracer.current() );
            return result;
        } );

        assertSame( result, returned );
        assertNull( Tracer.current() );

        final InOrder inOrder = inOrder( this.trace, this.manager );
        inOrder.verify( this.manager ).newTrace( "myTrace", null );
        inOrder.verify( this.trace ).start();
        inOrder.verify( this.manager ).dispatch( argThat( event -> event.getType() == TraceEvent.Type.START ) );
        inOrder.verify( this.trace ).end();
        inOrder.verify( this.manager ).dispatch( argThat( event -> event.getType() == TraceEvent.Type.END ) );
    }

    @Test
    void enabledTracesVoidCall()
        throws Throwable
    {
        when( this.manager.newTrace( eq( "myTrace" ), any() ) ).thenReturn( this.trace );

        final AtomicReference<Trace> seen = new AtomicReference<>();
        TraceSupport.trace( "myTrace", () -> seen.set( Tracer.current() ) );

        assertSame( this.trace, seen.get() );
        verify( this.trace ).start();
        verify( this.trace ).end();
    }

    @Test
    void checkedExceptionPropagatesUnchanged()
    {
        when( this.manager.newTrace( any(), any() ) ).thenReturn( this.trace );

        final IOException failure = new IOException( "io" );
        final Throwable thrown = assertThrows( IOException.class, () -> TraceSupport.trace( "test", () -> {
            throw failure;
        } ) );

        assertSame( failure, thrown );
        verify( this.trace ).end();
    }

    @Test
    void nestedCallsUseParent()
        throws Throwable
    {
        final Trace parent = mock( Trace.class );
        final Trace child = mock( Trace.class );
        when( this.manager.newTrace( eq( "parent" ), any() ) ).thenReturn( parent );
        when( this.manager.newTrace( eq( "child" ), any() ) ).thenReturn( child );

        TraceSupport.trace( "parent", () -> TraceSupport.trace( "child", () -> null ) );

        verify( this.manager ).newTrace( "parent", null );
        verify( this.manager ).newTrace( "child", parent );
    }
}
