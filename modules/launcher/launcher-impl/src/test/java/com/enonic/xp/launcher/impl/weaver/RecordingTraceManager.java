package com.enonic.xp.launcher.impl.weaver;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceLocation;
import com.enonic.xp.trace.TraceManager;

/**
 * TraceManager that records created traces and dispatched events for assertions.
 */
final class RecordingTraceManager
    implements TraceManager
{
    final List<TestTrace> traces = new CopyOnWriteArrayList<>();

    final List<TraceEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public Trace newTrace( final String name, final Trace parent )
    {
        final TestTrace trace = new TestTrace( name, parent );
        this.traces.add( trace );
        return trace;
    }

    @Override
    public void dispatch( final TraceEvent event )
    {
        this.events.add( event );
    }

    @Override
    public void enable( final boolean enabled )
    {
    }

    TestTrace singleTrace()
    {
        if ( this.traces.size() != 1 )
        {
            throw new AssertionError( "Expected exactly one trace, got " + this.traces );
        }
        return this.traces.get( 0 );
    }

    static final class TestTrace
        extends HashMap<String, Object>
        implements Trace
    {
        private final String id = UUID.randomUUID().toString();

        private final String name;

        private final Trace parent;

        private Instant startTime;

        private Instant endTime;

        private TestTrace( final String name, final Trace parent )
        {
            this.name = name;
            this.parent = parent;
        }

        Trace getParent()
        {
            return this.parent;
        }

        @Override
        public String getId()
        {
            return this.id;
        }

        @Override
        public String getParentId()
        {
            return this.parent != null ? this.parent.getId() : null;
        }

        @Override
        public String getName()
        {
            return this.name;
        }

        @Override
        public TraceLocation getLocation()
        {
            return null;
        }

        @Override
        public Instant getStartTime()
        {
            return this.startTime;
        }

        @Override
        public Instant getEndTime()
        {
            return this.endTime;
        }

        @Override
        public boolean inProgress()
        {
            return this.endTime == null;
        }

        @Override
        public Duration getDuration()
        {
            return this.startTime != null && this.endTime != null ? Duration.between( this.startTime, this.endTime ) : Duration.ZERO;
        }

        @Override
        public void start()
        {
            this.startTime = Instant.now();
        }

        @Override
        public void end()
        {
            this.endTime = Instant.now();
        }
    }
}
