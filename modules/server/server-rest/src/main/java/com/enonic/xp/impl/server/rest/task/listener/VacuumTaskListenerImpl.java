package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.vacuum.VacuumTaskListener;

public class VacuumTaskListenerImpl
    implements VacuumTaskListener
{
    private final ProgressReporter progressReporter;

    private int total = 0;

    private int current = 0;

    public VacuumTaskListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void total( final long total )
    {
        this.total = Math.toIntExact( total );
    }

    @Override
    public void taskExecuted()
    {
        current++;
        progressReporter.progress( current, total );

    }
}
