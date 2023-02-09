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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
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
}
