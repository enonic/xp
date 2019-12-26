package com.enonic.xp.task;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class TaskProgressReporterContext
{
    private final static ThreadLocal<ProgressReporter> CURRENT = new ThreadLocal<>();

    public static ProgressReporter current()
    {
        return CURRENT.get();
    }

    private static void set( final ProgressReporter req )
    {
        if ( req == null )
        {
            CURRENT.remove();
        }
        else
        {
            CURRENT.set( req );
        }
    }

    public static RunnableTask withContext( final RunnableTask runnableTask )
    {
        return ( id, progressReporter ) ->
        {
            final ProgressReporter old = current();
            set( progressReporter );

            try
            {
                runnableTask.run( id, progressReporter );
            }
            finally
            {
                set( old );
            }
        };
    }
}
