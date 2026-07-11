package com.enonic.xp.task;

public final class TaskProgressReporterContext
{
    private static final ScopedValue<ProgressReporter> CURRENT = ScopedValue.newInstance();

    public static ProgressReporter current()
    {
        return CURRENT.isBound() ? CURRENT.get() : null;
    }

    public static RunnableTask withContext( final RunnableTask runnableTask )
    {
        return ( id, progressReporter ) -> ScopedValue.where( CURRENT, progressReporter )
            .run( () -> runnableTask.run( id, progressReporter ) );
    }
}
