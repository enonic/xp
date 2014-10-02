package com.enonic.wem.api.context;

import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.workspace.Workspace;

public final class Context2
{
    private final static Context2 DEFAULT = new Context2();

    private final static ThreadLocal<Context2> CURRENT = new ThreadLocal<Context2>()
    {
        @Override
        protected Context2 initialValue()
        {
            return DEFAULT;
        }
    };

    protected final Map<String, Object> objects = Maps.newHashMap();

    // Should be id
    public Repository getRepository()
    {
        return getObject( Repository.class );
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
        final Context2 old = CURRENT.get();
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
        final Context2 old = CURRENT.get();
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

    public static Context2 current()
    {
        return CURRENT.get();
    }
}

