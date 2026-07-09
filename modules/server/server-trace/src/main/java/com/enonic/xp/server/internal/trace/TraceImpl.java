package com.enonic.xp.server.internal.trace;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceLocation;

/**
 * Attributes are stored in a ConcurrentHashMap: trace events are dispatched to listeners on another thread while
 * the traced code may still be adding attributes, so the map must be safe for concurrent reads and iteration.
 * Null values (which ConcurrentHashMap rejects) are treated as removals to keep the lenient Map contract that
 * trace enrichment code relies on.
 */
final class TraceImpl
    extends ConcurrentHashMap<String, Object>
    implements Trace
{
    private final String id;

    private final String parentId;

    private final String name;

    private Instant startTime;

    private long startTimeNano;

    private Instant endTime;

    private long endTimeNano;

    private final TraceLocation location;

    TraceImpl( final String name, final String parentId, final TraceLocation location )
    {
        this.id = UUID.randomUUID().toString();
        this.parentId = parentId;
        this.name = name;
        this.location = location;
    }

    @Override
    public Object put( final String key, final Object value )
    {
        if ( value == null )
        {
            return remove( key );
        }
        return super.put( key, value );
    }

    @Override
    public void putAll( final Map<? extends String, ?> map )
    {
        map.forEach( this::put );
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getParentId()
    {
        return this.parentId;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public TraceLocation getLocation()
    {
        return this.location;
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
    public Duration getDuration()
    {
        if ( this.startTime == null )
        {
            return Duration.ZERO;
        }

        if ( this.endTime == null )
        {
            return Duration.ofNanos( System.nanoTime() - this.startTimeNano );
        }

        return Duration.ofNanos( this.endTimeNano - this.startTimeNano );
    }

    @Override
    public boolean inProgress()
    {
        return this.endTime == null;
    }

    @Override
    public void start()
    {
        this.startTime = Instant.now();
        this.startTimeNano = System.nanoTime();
    }

    @Override
    public void end()
    {
        this.endTime = Instant.now();
        this.endTimeNano = System.nanoTime();
    }
}
