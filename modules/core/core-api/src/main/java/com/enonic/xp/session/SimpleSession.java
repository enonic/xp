package com.enonic.xp.session;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class SimpleSession
    implements Session
{
    private final SessionKey key;

    private final Map<String, Object> attributes;

    public SimpleSession( final SessionKey key )
    {
        this.key = key;
        this.attributes = new HashMap<>();
    }

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
}
