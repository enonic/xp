package com.enonic.xp.trace;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TracerTest
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
    void testEnabled()
    {
        assertTrue( Tracer.isEnabled() );

        Tracer.setManager( null );
        assertFalse( Tracer.isEnabled() );
    }

    @Test
    void testNewTrace()
    {
        when( this.manager.newTrace( any(), any() ) ).thenReturn( trace );

        assertSame( this.trace, Tracer.newTrace( "test" ) );

        Tracer.setManager( null );
        assertNull( Tracer.newTrace( "test" ) );
    }

    @Test
    void testCurrent()
    {
        assertNull( Tracer.current() );
        Tracer.trace( this.trace, () -> assertSame( this.trace, Tracer.current() ) );
    }

    @Test
    void withCurrent()
    {
        Tracer.withCurrent( ( t ) ->
                            {
                            } );

        assertNull( Tracer.current() );
        Tracer.trace( this.trace, () -> Tracer.withCurrent( ( t ) -> assertSame( this.trace, t ) ) );
    }

    @Test
    void traceNull()
    {
        Tracer.trace( (Trace) null, () ->
        {
        } );
    }

    @Test
    void trace_disabled( @Mock final Consumer<Trace> before, @Mock final BiConsumer<Trace, Object> after, @Mock final Supplier<Object> call )
    {
        Tracer.setManager( null );

        Tracer.trace( "disabled", before, call, after );
        verifyNoInteractions( before, after );
        verify( call, times( 1 ) ).get();
    }

    @Test
    void trace_disabled( @Mock final Consumer<Trace> before, @Mock final Supplier<Object> call )
    {
        Tracer.setManager( null );

        Tracer.trace( "disabled", before, call );
        verifyNoInteractions( before );
        verify( call, times( 1 ) ).get();
    }

    @Test
    void trace_enabled( @Mock final Consumer<Trace> before, @Mock final BiConsumer<Trace, Object> after, @Mock final Supplier<Object> call )
    {
        when( this.manager.newTrace( eq("enabled"), any() ) ).thenReturn( trace );

        Object result = mock( Object.class );
        when( call.get() ).thenReturn( result );

        Tracer.trace( "enabled", before, call, after );

        final InOrder inOrder = inOrder( before, after, call, result );
        inOrder.verify( before, times( 1 ) ).accept( same( trace ) );
        inOrder.verify( call, times( 1 ) ).get();
        inOrder.verify( after, times( 1 ) ).accept( same( trace ), same( result ) );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void trace_enabled( @Mock final Consumer<Trace> before, @Mock final Supplier<Object> call )
    {
        when( this.manager.newTrace( eq("enabled"), any() ) ).thenReturn( trace );

        Object result = mock( Object.class );
        when( call.get() ).thenReturn( result );

        Tracer.trace( "enabled", before, call );

        final InOrder inOrder = inOrder( before, call, result );
        inOrder.verify( before, times( 1 ) ).accept( same( trace ) );
        inOrder.verify( call, times( 1 ) ).get();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testTrace()
        throws Exception
    {
        when( this.manager.newTrace( any(), any() ) ).thenReturn( trace );

        Tracer.trace( "test", () -> assertSame( this.trace, Tracer.current() ) );

        final int return1 = Tracer.trace( "test", () -> 1 );
        assertEquals( 1, return1 );

        final int return2 = Tracer.traceEx( "test", () -> 2 );
        assertEquals( 2, return2 );
    }

    @Test
    void currentIsScopedToTrace()
    {
        final Trace outer = mock( Trace.class );
        final Trace inner = mock( Trace.class );

        Tracer.trace( outer, () -> {
            assertSame( outer, Tracer.current() );
            Tracer.trace( inner, () -> assertSame( inner, Tracer.current() ) );
            assertSame( outer, Tracer.current() );
        } );

        assertNull( Tracer.current() );
    }

    @Test
    void currentRestoredWhenTraceThrows()
    {
        final IllegalStateException failure = new IllegalStateException( "failed" );

        final IllegalStateException thrown = assertThrows( IllegalStateException.class, () -> Tracer.trace( this.trace, () -> {
            throw failure;
        } ) );

        assertSame( failure, thrown );
        assertNull( Tracer.current() );

        final InOrder inOrder = inOrder( this.trace, this.manager );
        inOrder.verify( this.trace ).start();
        inOrder.verify( this.manager ).dispatch( argThat( event -> event.getType() == TraceEvent.Type.START ) );
        inOrder.verify( this.trace ).end();
        inOrder.verify( this.manager ).dispatch( argThat( event -> event.getType() == TraceEvent.Type.END ) );
    }

    @Test
    void traceExPropagatesCheckedException()
    {
        final Exception failure = new Exception( "checked" );

        final Exception thrown = assertThrows( Exception.class, () -> Tracer.traceEx( this.trace, () -> {
            throw failure;
        } ) );

        assertSame( failure, thrown );
    }

    @Test
    void traceIOPropagatesIOException()
    {
        final java.io.IOException failure = new java.io.IOException( "io" );

        final java.io.IOException thrown = assertThrows( java.io.IOException.class, () -> Tracer.traceIO( this.trace, () -> {
            throw failure;
        } ) );

        assertSame( failure, thrown );
    }

    @Test
    void staticAttributeRecordsOnCurrentTrace()
    {
        // no trace bound: all no-ops
        Tracer.attribute( "string", "value" );
        Tracer.attribute( "long", 1L );
        Tracer.attribute( "boolean", true );
        verifyNoInteractions( this.trace );

        Tracer.trace( this.trace, () -> {
            Tracer.attribute( "string", "value" );
            Tracer.attribute( "long", 1L );
            Tracer.attribute( "double", 1.5d );
            Tracer.attribute( "boolean", true );
            Tracer.attribute( "list", java.util.List.of( "a" ) );
        } );

        verify( this.trace ).attribute( "string", "value" );
        verify( this.trace ).attribute( "long", 1L );
        verify( this.trace ).attribute( "double", 1.5d );
        verify( this.trace ).attribute( "boolean", true );
        verify( this.trace ).attribute( "list", java.util.List.of( "a" ) );
    }

    @Test
    void disabledTracingShieldsEnclosingTrace()
    {
        Tracer.trace( this.trace, () -> {
            // tracing gets disabled while the outer trace is still bound
            Tracer.setManager( null );
            Tracer.trace( "inner", () -> {
                assertNull( Tracer.current() );
                Tracer.withCurrent( t -> t.attribute( "polluted", true ) );
            } );
        } );

        verify( this.trace, never() ).attribute( anyString(), anyBoolean() );
    }

    @Test
    void exceptionsPropagateUnchangedRegardlessOfTracing()
    {
        final java.io.IOException checked = new java.io.IOException( "checked" );

        // tracing never alters what a caller catches - even a sneaky-thrown checked exception stays as-is
        final java.io.IOException thrownTraced =
            assertThrows( java.io.IOException.class, () -> Tracer.trace( this.trace, () -> sneakyThrow( checked ) ) );
        assertSame( checked, thrownTraced );

        Tracer.setManager( null );
        final java.io.IOException thrownUntraced =
            assertThrows( java.io.IOException.class, () -> Tracer.trace( (Trace) null, () -> sneakyThrow( checked ) ) );
        assertSame( checked, thrownUntraced );
    }

    @SuppressWarnings("unchecked")
    private static <T, X extends Throwable> T sneakyThrow( final Throwable t )
        throws X
    {
        throw (X) t;
    }

    @Test
    void newTraceUsesCurrentAsParent()
    {
        when( this.manager.newTrace( any(), any() ) ).thenReturn( this.trace );

        Tracer.trace( this.trace, () -> Tracer.newTrace( "child" ) );

        verify( this.manager ).newTrace( "child", this.trace );
    }
}
