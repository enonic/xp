package com.enonic.xp.lib.task;

import com.enonic.xp.task.ProgressReporter;


final class TaskProgressReporterHolder
{
    private final static ThreadLocal<ProgressReporter> CURRENT = new ThreadLocal<>();

    static ProgressReporter get()
    {
        return CURRENT.get();
    }

    static void set( final ProgressReporter req )
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
}
