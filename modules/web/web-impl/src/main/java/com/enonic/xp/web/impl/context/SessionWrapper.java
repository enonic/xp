package com.enonic.xp.web.impl.context;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionKey;

final class SessionWrapper
    implements Session
{
    private final HttpSession httpSession;

    public SessionWrapper( final HttpSession httpSession )
    {
        this.httpSession = httpSession;
    }

    @Override
    public SessionKey getKey()
    {
        return SessionKey.from( this.httpSession.getId() );
    }

    @Override
    public Object getAttribute( final String key )
    {
        try
        {
            return this.httpSession.getAttribute( key );
        }
        catch ( IllegalStateException e )
        {
            return null;
        }
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
        this.httpSession.setAttribute( key, value );
    }

    @Override
    public <T> void setAttribute( final T value )
    {
        setAttribute( value.getClass().getName(), value );
    }

    @Override
    public void removeAttribute( final String key )
    {
        this.httpSession.removeAttribute( key );
    }

    @Override
    public <T> void removeAttribute( final Class<T> type )
    {
        removeAttribute( type.getName() );
    }

    @Override
    public Map<String, Object> getAttributes()
    {
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        final Enumeration<String> names = this.httpSession.getAttributeNames();

        while ( names.hasMoreElements() )
        {
            final String key = names.nextElement();
            builder.put( key, this.httpSession.getAttribute( key ) );
        }

        return builder.build();
    }

    @Override
    public void invalidate()
    {
        this.httpSession.invalidate();
    }
}
