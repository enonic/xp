package com.enonic.xp.web.impl.context;

import java.util.Enumeration;
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

    private HttpSession session;

    SessionWrapper( final HttpServletRequest request )
    {
        this.request = request;
        this.session = this.request.getSession( false );
    }

    private void createSessionIfNeeded()
    {
        if ( this.session != null )
        {
            return;
        }

        this.session = this.request.getSession( true );
    }

    @Override
    public SessionKey getKey()
    {
        createSessionIfNeeded();
        return SessionKey.from( this.session.getId() );
    }

    @Override
    public Object getAttribute( final String key )
    {
        if ( this.session == null )
        {
            return null;
        }

        try
        {
            return this.session.getAttribute( key );
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
        createSessionIfNeeded();
        this.session.setAttribute( key, value );
    }

    @Override
    public <T> void setAttribute( final T value )
    {
        setAttribute( value.getClass().getName(), value );
    }

    @Override
    public void removeAttribute( final String key )
    {
        if ( this.session == null )
        {
            return;
        }

        this.session.removeAttribute( key );
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
        if ( this.session == null )
        {
            return builder.build();
        }

        final Enumeration<String> names = this.session.getAttributeNames();

        while ( names.hasMoreElements() )
        {
            final String key = names.nextElement();
            builder.put( key, this.session.getAttribute( key ) );
        }

        return builder.build();
    }

    @Override
    public void invalidate()
    {
        if ( this.session == null )
        {
            return;
        }

        this.session.invalidate();
    }
}
