package com.enonic.xp.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableMap;

public class SessionMock
    implements Session
{
    private static final AtomicInteger sessionCounter = new AtomicInteger( 0 );

    private SessionKey key = SessionKey.from( "mock-session" );

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    private int maxInactiveInterval = -1;

    @Override
    public SessionKey getKey()
    {
        return this.key;
    }

    @Override
    public Object getAttribute( final String key )
    {
        return this.attributes.get( key );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute( final Class<T> type )
    {
        return (T) getAttribute( type.getName() );
    }

    @Override
    public void setAttribute( final String key, final Object value )
    {
        this.attributes.put( key, value );
    }

    @Override
    public <T> void setAttribute( final T value )
    {
        setAttribute( value.getClass().getName(), value );
    }

    @Override
    public Map<String, Object> getAttributes()
    {
        return ImmutableMap.copyOf( this.attributes );
    }

    @Override
    public void removeAttribute( final String key )
    {
        this.attributes.remove( key );
    }

    @Override
    public <T> void removeAttribute( final Class<T> type )
    {
        removeAttribute( type.getName() );
    }

    @Override
    public void invalidate()
    {
        this.attributes.clear();
    }

    @Override
    public String changeSessionId()
    {
        this.key = SessionKey.from( "mock-session-" + sessionCounter.incrementAndGet() );
        return this.key.toString();
    }

    @Override
    public void setMaxInactiveInterval( final int seconds )
    {
        this.maxInactiveInterval = seconds;
    }

    public int getMaxInactiveInterval()
    {
        return this.maxInactiveInterval;
    }
}
