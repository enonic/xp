package com.enonic.wem.api.context;

import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

public final class Context
{
    private final static ThreadLocal<Context> CURRENT = new ThreadLocal<Context>()
    {
        @Override
        protected Context initialValue()
        {
            return ContentConstants.CONTEXT_STAGE;
        }
    };

    protected final Map<String, Object> objects = Maps.newHashMap();

    // Should be id
    public RepositoryId getRepositoryId()
    {
        return getObject( RepositoryId.class );
    }

    public Workspace getWorkspace()
    {
        return getObject( Workspace.class );
    }

    public <T> T getObject( final Class<T> type )
    {
        return getObject( type.getName() );
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject( final String key )
    {
        return (T) this.objects.get( key );
    }

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

    public <T> T runWith( final Callable<T> runnable )
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
            throw Throwables.propagate( e );
        }
        finally
        {
            CURRENT.set( old );
        }
    }

    public static Context current()
    {
        if ( CURRENT.get() == null )
        {
            throw new IllegalStateException( "No context set" );
        }

        return CURRENT.get();
    }
}

