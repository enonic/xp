package com.enonic.wem.api.context;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.session.Session;

final class LocalScopeImpl
    implements LocalScope
{
    private final Map<String, Object> attributes;

    private Session session;

    public LocalScopeImpl()
    {
        this.attributes = Maps.newHashMap();
    }

    @Override
    public Session getSession()
    {
        return this.session;
    }

    @Override
    public void setSession( final Session session )
    {
        this.session = session;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T getAttribute( final Class<T> type )
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
    public Object getAttribute( final String key )
    {
        final Object value = this.attributes.get( key );
        if ( value != null )
        {
            return value;
        }

        if ( this.session != null )
        {
            return this.session.getAttribute( key );
        }

        return null;
    }

    @Override
    public Map<String, Object> getAttributes()
    {
        return ImmutableMap.copyOf( this.attributes );
    }
}
