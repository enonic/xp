package com.enonic.xp.web.impl.context;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionKey;

final class SessionWrapper
    implements Session
{
    private final HttpServletRequest request;

    SessionWrapper( final HttpServletRequest request )
    {
        this.request = request;
    }

    @Override
    public SessionKey getKey()
    {
        return SessionKey.from( request.getSession().getId() );
    }

    @Override
    public Object getAttribute( final String key )
    {
        HttpSession session = request.getSession( false );
        if ( session == null )
        {
            return null;
        }

        try
        {
            return session.getAttribute( key );
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
        HttpSession session = request.getSession( true );
        session.setAttribute( key, value );
    }

    @Override
    public <T> void setAttribute( final T value )
    {
        setAttribute( value.getClass().getName(), value );
    }

    @Override
    public void removeAttribute( final String key )
    {
        HttpSession session = request.getSession( false );
        if ( session == null )
        {
            return;
        }

        session.removeAttribute( key );
    }

    @Override
    public <T> void removeAttribute( final Class<T> type )
    {
        removeAttribute( type.getName() );
    }

    @Override
    public Map<String, Object> getAttributes()
    {
        HttpSession session = request.getSession( false );

        if ( session == null )
        {
            return ImmutableMap.of();
        }
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        final Iterator<String> names = session.getAttributeNames().asIterator();

        while ( names.hasNext() )
        {
            final String key = names.next();
            builder.put( key, session.getAttribute( key ) );
        }

        return builder.build();
    }

    @Override
    public void invalidate()
    {
        HttpSession session = request.getSession( false );
        if ( session == null )
        {
            return;
        }

        session.invalidate();
    }
}
