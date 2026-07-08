package com.enonic.xp.launcher.impl.weaver;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.server.internal.trace.TraceService;
import com.enonic.xp.server.internal.trace.event.TraceEventDispatcherExecutorImpl;
import com.enonic.xp.server.internal.trace.event.TraceEventDispatcherImpl;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.Tracer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end test of the tracing pipeline: a class with {@code @Traced} methods is woven by
 * {@link TraceWeaverTransformer} exactly as the OSGi {@link TraceWeaver} hook does at load time, executed against
 * the real trace subsystem from server-trace ({@link TraceService}, async {@link TraceEventDispatcherImpl}), and
 * the resulting trace events are asserted on a registered {@link com.enonic.xp.trace.TraceListener} - proving
 * that traces created by woven code get delivered to listeners.
 */
class TraceEndToEndTest
{
    private TraceEventDispatcherExecutorImpl executor;

    private TraceEventDispatcherImpl dispatcher;

    private TraceService traceService;

    private List<TraceEvent> received;

    @BeforeEach
    void setUp()
    {
        this.executor = new TraceEventDispatcherExecutorImpl();
        this.dispatcher = new TraceEventDispatcherImpl( this.executor );
        this.received = new CopyOnWriteArrayList<>();

        this.traceService = new TraceService();
        this.traceService.setDispatcher( this.dispatcher );
        this.traceService.enable( true );
    }

    @AfterEach
    void tearDown()
    {
        this.traceService.enable( false );
        this.executor.deactivate();
    }

    @Test
    void wovenTracesAreDeliveredToListener()
        throws Exception
    {
        // nested() opens "fixture.nested" which calls hello() opening "fixture.hello": 2 traces, 4 events
        final CountDownLatch delivered = expectEvents( 4 );

        final TracedFixtureApi fixture = newWovenFixture();
        assertEquals( "Hello world", fixture.nested( "world" ) );

        assertTrue( delivered.await( 30, TimeUnit.SECONDS ), "trace events were not delivered to the listener" );

        final Trace nested = singleTrace( "fixture.nested" );
        final Trace hello = singleTrace( "fixture.hello" );

        // start and end delivered for each trace
        assertEquals( 2, this.received.stream().filter( e -> e.getTrace() == nested ).count() );
        assertEquals( 2, this.received.stream().filter( e -> e.getTrace() == hello ).count() );
        assertEquals( TraceEvent.Type.START, this.received.get( 0 ).getType() );
        assertEquals( TraceEvent.Type.END, this.received.get( this.received.size() - 1 ).getType() );

        // parent-child propagation through the ScopedValue scope
        assertNotNull( nested.getId() );
        assertEquals( nested.getId(), hello.getParentId() );

        // attribute enrichment from inside the woven method (Tracer.withCurrent)
        assertEquals( "world", hello.get( "who" ) );

        // traces are ended and carry timing
        assertTrue( !nested.inProgress() && !hello.inProgress() );
        assertNotNull( nested.getStartTime() );
        assertNotNull( nested.getEndTime() );

        // trace location points at the annotated method, not at the tracing plumbing
        assertEquals( TracedFixture.class.getName(), hello.getLocation().getClassName() );
        assertEquals( "hello", hello.getLocation().getMethod() );
    }

    @Test
    void externalTracerApiStillDelivers()
        throws Exception
    {
        // Backwards compatibility: the pre-existing public Tracer API delivers through the same pipeline.
        final CountDownLatch delivered = expectEvents( 2 );

        final String result = Tracer.trace( "legacyExternal", () -> {
            Tracer.withCurrent( trace -> trace.put( "legacy", true ) );
            return "ok";
        } );

        assertEquals( "ok", result );
        assertTrue( delivered.await( 30, TimeUnit.SECONDS ), "trace events were not delivered to the listener" );

        final Trace trace = singleTrace( "legacyExternal" );
        assertEquals( Boolean.TRUE, trace.get( "legacy" ) );
        assertEquals( TraceEvent.Type.START, this.received.get( 0 ).getType() );
        assertEquals( TraceEvent.Type.END, this.received.get( 1 ).getType() );
    }

    @Test
    void wovenAndExternalTracesNest()
        throws Exception
    {
        // A woven method called inside an external Tracer scope gets the external trace as parent.
        final CountDownLatch delivered = expectEvents( 4 );

        final TracedFixtureApi fixture = newWovenFixture();
        final String result = Tracer.trace( "outerExternal", () -> fixture.hello( "mixed" ) );

        assertEquals( "Hello mixed", result );
        assertTrue( delivered.await( 30, TimeUnit.SECONDS ), "trace events were not delivered to the listener" );

        final Trace outer = singleTrace( "outerExternal" );
        final Trace inner = singleTrace( "fixture.hello" );
        assertEquals( outer.getId(), inner.getParentId() );
    }

    private CountDownLatch expectEvents( final int count )
    {
        final CountDownLatch latch = new CountDownLatch( count );
        this.dispatcher.addListener( event -> {
            this.received.add( event );
            latch.countDown();
        } );
        return latch;
    }

    private Trace singleTrace( final String name )
    {
        final List<Trace> matches =
            this.received.stream().map( TraceEvent::getTrace ).filter( t -> name.equals( t.getName() ) ).distinct().toList();
        assertEquals( 1, matches.size(), "expected exactly one trace named " + name );
        return matches.get( 0 );
    }

    private static TracedFixtureApi newWovenFixture()
        throws Exception
    {
        final byte[] woven = TraceWeaverTransformer.transform( TraceWeaverTransformerTest.fixtureBytes() );
        assertNotNull( woven );

        final WovenClassLoader loader =
            new WovenClassLoader( TracedFixture.class.getName(), woven, TraceEndToEndTest.class.getClassLoader() );
        return (TracedFixtureApi) loader.loadClass( TracedFixture.class.getName() ).getConstructor().newInstance();
    }
}
