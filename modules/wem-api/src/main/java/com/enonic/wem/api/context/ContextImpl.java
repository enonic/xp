package com.enonic.wem.api.context;

import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.session.Session;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.api.workspace.Workspace;

final class ContextImpl
    implements Context
{
    private final static ContextAccessor CURRENT = ContextAccessor.INSTANCE;

    private final Map<String, Object> attributes;

    private Session session;

    public ContextImpl()
    {
        this.attributes = Maps.newHashMap();
    }

    @Override
    public RepositoryId getRepositoryId()
    {
        return getAttribute( RepositoryId.class );
    }

    @Override
    public Workspace getWorkspace()
    {
        return getAttribute( Workspace.class );
    }

    @Override
    public AuthenticationInfo getAuthInfo()
    {
        return getAttribute( AuthenticationInfo.class );
    }

    @Override
    public Session getSession()
    {
        return this.session;
    }

    @Override
    public Object getAttribute( final String key )
    {
        final Object value = this.attributes.get( key );
        if ( value != null )
        {
            return value;
        }

        if ( this.session == null )
        {
            return null;
        }

        return this.session.getAttribute( key );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute( final Class<T> type )
    {
        return (T) getAttribute( type.getName() );
    }

    @Override
    public Map<String, Object> getAttributes()
    {
        return ImmutableMap.copyOf( this.attributes );
    }

    protected <T> void setAttribute( final T value )
    {
        setAttribute( value.getClass().getName(), value );
    }

    protected void setAttribute( final String key, final Object value )
    {
        this.attributes.put( key, value );
    }

    protected void setAttributes( final Map<String, Object> values )
    {
        this.attributes.putAll( values );
    }

    protected void setSession( final Session session )
    {
        this.session = session;
    }

    @Override
    public void runWith( final Runnable runnable )
    {
        final Context old = CURRENT.get();
        CURRENT.set( this );

        try
        {
            runnable.run();
        }
        finally
        {
            CURRENT.set( old );
        }
    }

    @Override
    public <T> T callWith( final Callable<T> runnable )
    {
        final Context old = CURRENT.get();
        CURRENT.set( this );

        try
        {
            return runnable.call();
        }
        catch ( final RuntimeException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
        finally
        {
            CURRENT.set( old );
        }
    }
}
