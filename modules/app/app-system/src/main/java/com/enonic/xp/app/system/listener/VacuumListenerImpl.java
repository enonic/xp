package com.enonic.xp.app.system.listener;

import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.vacuum.VacuumListener;

public class VacuumListenerImpl
    implements VacuumListener
{
    private final ProgressReporter progressReporter;

    private int total;

    private int current;

    public VacuumListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void vacuumBegin( final long taskCount )
    {
        total = Math.toIntExact( taskCount );
    }

    @Override
    public void taskBegin( final String task, final Long stepCount )
    {
        progressReporter.progress( current, total );
        current++;
    }

    @Override
    public void stepBegin( final String stepName, final Long toProcessCount )
    {

    }

    @Override
    public void processed( final long count )
    {

    }
}
