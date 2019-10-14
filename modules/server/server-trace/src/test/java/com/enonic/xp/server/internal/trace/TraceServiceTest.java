package com.enonic.xp.server.internal.trace;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.server.internal.trace.event.TraceEventDispatcher;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.Tracer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TraceServiceTest
{
    private TraceEventDispatcher dispatcher;

    private TraceService service;

    @BeforeEach
    public void setUp()
    {
        this.dispatcher = Mockito.mock( TraceEventDispatcher.class );
        this.service = new TraceService();
        this.service.setDispatcher( this.dispatcher );
    }

    @AfterEach
    public void tearDown()
    {
        Tracer.setManager( null );
    }

    @Test
    public void testDisabled()
    {
        final TraceConfig config = Mockito.mock( TraceConfig.class );
        Mockito.when( config.enabled() ).thenReturn( false );

        this.service.activate( config );
        assertFalse( Tracer.isEnabled() );
    }

    @Test
    public void testEnabled()
    {
        final TraceConfig config = Mockito.mock( TraceConfig.class );
        Mockito.when( config.enabled() ).thenReturn( true );

        this.service.activate( config );
        assertTrue( Tracer.isEnabled() );

        this.service.deactivate();
        assertFalse( Tracer.isEnabled() );
    }

    @Test
    public void testNewTrace()
    {
        final Trace trace = this.service.newTrace( "test", null );
        assertNotNull( trace );
    }

    @Test
    public void testDispatch()
    {
        final TraceEvent event = TraceEvent.start( null );
        this.service.dispatch( event );

        Mockito.verify( this.dispatcher, Mockito.times( 1 ) ).queue( event );
    }

    @Test
    public void testEnableTracing()
    {
        assertFalse( Tracer.isEnabled() );
        this.service.enable( true );

        assertTrue( Tracer.isEnabled() );
    }

    @Test
    public void testDisableTracing()
    {
        final TraceConfig config = Mockito.mock( TraceConfig.class );
        Mockito.when( config.enabled() ).thenReturn( true );

        this.service.activate( config );
        assertTrue( Tracer.isEnabled() );

        this.service.enable( false );

        assertFalse( Tracer.isEnabled() );
    }
}
