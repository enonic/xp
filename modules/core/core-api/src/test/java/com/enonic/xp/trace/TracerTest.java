package com.enonic.xp.trace;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TracerTest
{
    private TraceManager manager;

    private Trace trace;

    @BeforeEach
    public void setUp()
    {
        this.manager = Mockito.mock( TraceManager.class );
        Tracer.setManager( this.manager );

        this.trace = Mockito.mock( Trace.class );
        Mockito.when( this.manager.newTrace( Mockito.any(), Mockito.any() ) ).thenReturn( trace );
    }

    @AfterEach
    public void tearDown()
    {
        Tracer.setManager( null );
    }

    @Test
    public void testEnabled()
    {
        assertTrue( Tracer.isEnabled() );

        Tracer.setManager( null );
        assertFalse( Tracer.isEnabled() );
    }

    @Test
    public void testNewTrace()
    {
        assertSame( this.trace, Tracer.newTrace( "test" ) );

        Tracer.setManager( null );
        assertNull( Tracer.newTrace( "test" ) );
    }

    @Test
    public void testCurrent()
    {
        assertNull( Tracer.current() );
        Tracer.trace( this.trace, () -> assertSame( this.trace, Tracer.current() ) );
    }

    @Test
    public void withCurrent()
    {
        Tracer.withCurrent( ( t ) ->
                            {
                            } );

        assertNull( Tracer.current() );
        Tracer.trace( this.trace, () -> Tracer.withCurrent( ( t ) -> assertSame( this.trace, t ) ) );
    }

    @Test
    public void traceNull()
    {
        Tracer.trace( (Trace) null, () ->
        {
        } );
    }

    @Test
    public void testTrace()
        throws Exception
    {
        Tracer.trace( "test", () -> assertSame( this.trace, Tracer.current() ) );

        final int return1 = Tracer.trace( "test", () -> 1 );
        assertEquals( 1, return1 );

        final int return2 = Tracer.traceEx( "test", () -> 2 );
        assertEquals( 2, return2 );
    }
}
