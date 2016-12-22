package com.enonic.xp.server.internal.trace.manager;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceLocation;

final class TraceImpl
    extends HashMap<String, Object>
    implements Trace
{
    private final String id;

    private final String parentId;

    private final String type;

    private Instant startTime;

    private Instant endTime;

    private final TraceLocation location;

    TraceImpl( final String type, final String parentId, final TraceLocation location )
    {
        this.id = UUID.randomUUID().toString();
        this.parentId = parentId;
        this.type = type;
        this.location = location;
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
    public String getType()
    {
        return this.type;
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
            return Duration.between( this.startTime, Instant.now() );
        }

        return Duration.between( this.startTime, this.endTime );
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
    }

    @Override
    public void end()
    {
        this.endTime = Instant.now();
    }
}
