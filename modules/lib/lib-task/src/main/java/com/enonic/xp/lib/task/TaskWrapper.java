package com.enonic.xp.lib.task;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;

final class TaskWrapper
    implements RunnableTask
{
    private final static Logger LOG = LoggerFactory.getLogger( SubmitTaskHandler.class );

    private final Function<Void, Void> taskFunction;

    private final String description;

    public TaskWrapper( final Function<Void, Void> taskFunction, final String description )
    {
        this.taskFunction = taskFunction;
        this.description = description;
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        TaskProgressReporterContext.withContext( this::runTask ).run( id, progressReporter );
    }

    private void runTask( final TaskId id, final ProgressReporter progressReporter )
    {
        try
        {
            taskFunction.apply( null );
        }
        catch ( Throwable t )
        {
            LOG.error( "Error executing task [{}] '{}': {}", id.toString(), description, t.getMessage(), t );
            throw t;
        }
    }
}
