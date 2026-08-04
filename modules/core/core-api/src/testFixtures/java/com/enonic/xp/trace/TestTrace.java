package com.enonic.xp.trace;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Real, map-backed {@link Trace} for tests.
 * <p>
 * Binding it with {@code Tracer.trace( trace, ... )} (or {@code traceEx}/{@code traceIO}) makes
 * {@link Tracer#withCurrent} enrichment code execute for real - attribute expressions run and their values can be
 * asserted directly on the map. Prefer this over mocking {@link Trace}: mocks do not execute the typed
 * {@code attribute} default methods and cannot catch broken attribute expressions.
 */
@NullMarked
public final class TestTrace
    extends ConcurrentHashMap<String, Object>
    implements Trace
{
    private final String id = UUID.randomUUID().toString();

    private final @Nullable String parentId;

    private final String name;

    private @Nullable Instant startTime;

    private @Nullable Instant endTime;

    private TestTrace( final String name, final @Nullable String parentId )
    {
        this.name = name;
        this.parentId = parentId;
    }

    public static TestTrace of( final String name )
    {
        return new TestTrace( name, null );
    }

    public static TestTrace of( final String name, final @Nullable String parentId )
    {
        return new TestTrace( name, parentId );
    }

    @Deprecated
    @Override
    public @Nullable Object put( final String key, final @Nullable Object value )
    {
        if ( value == null )
        {
            return remove( key );
        }
        return super.put( key, value );
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public @Nullable String getParentId()
    {
        return this.parentId;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public @Nullable TraceLocation getLocation()
    {
        return null;
    }

    @Override
    public @Nullable Instant getStartTime()
    {
        return this.startTime;
    }

    @Override
    public @Nullable Instant getEndTime()
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
