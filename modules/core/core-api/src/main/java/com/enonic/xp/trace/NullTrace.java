package com.enonic.xp.trace;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Set;

final class NullTrace extends AbstractMap<String, Object>
    implements Trace
{
    public static final NullTrace INSTANCE = new NullTrace();

    private NullTrace()
    {
    }

    @Override
    public Set<Entry<String, Object>> entrySet()
    {
        return Collections.emptySet();
    }

    @Override
    public Object put( final String key, final Object value )
    {
        return null;
    }

    @Override
    public String getId()
    {
        return null;
    }

    @Override
    public String getParentId()
    {
        return null;
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public TraceLocation getLocation()
    {
        return null;
    }

    @Override
    public Instant getStartTime()
    {
        return null;
    }

    @Override
    public Instant getEndTime()
    {
        return null;
    }

    @Override
    public boolean inProgress()
    {
        return false;
    }

    @Override
    public Duration getDuration()
    {
        return Duration.ZERO;
    }

    @Override
    public void start()
    {
    }

    @Override
    public void end()
    {
    }
}
