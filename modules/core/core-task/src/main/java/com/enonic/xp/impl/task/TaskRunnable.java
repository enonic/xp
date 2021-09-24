package com.enonic.xp.impl.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.impl.task.distributed.DescribedTask;
import com.enonic.xp.impl.task.distributed.TaskContext;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

final class TaskRunnable
    implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger( TaskRunnable.class );

    private final DescribedTask runnableTask;

    private final InternalProgressReporter progressReporter;

    private final TaskId id;

    TaskRunnable( final DescribedTask runnableTask, final InternalProgressReporter progressReporter )
    {
        this.runnableTask = runnableTask;
        this.progressReporter = progressReporter;
        this.id = runnableTask.getTaskId();
    }

    @Override
    public void run()
    {
        setThreadName();
        final Trace trace = Tracer.newTrace( "task.run" );
        if ( trace == null )
        {
            doRun();
        }
        else
        {
            trace.put( "taskId", id );
            trace.put( "user", runnableTask.getTaskContext().getAuthInfo().getUser() != null ? runnableTask.getTaskContext()
                .getAuthInfo()
                .getUser()
                .getKey() : PrincipalKey.ofAnonymous() );
            trace.put( "app", runnableTask.getApplicationKey() );
            Tracer.trace( trace, this::doRun );
        }
    }

    private void doRun()
    {
        progressReporter.running();
        try
        {
            newContext().runWith( () -> runnableTask.run( progressReporter ) );
            progressReporter.finished();
        }
        catch ( Throwable t )
        {
            progressReporter.failed( t.getMessage() );
            LOG.error( "Error executing task [{}] '{}': {}", id, runnableTask.getDescription(), t.getMessage(), t );
        }
    }

    private Context newContext()
    {
        final TaskContext taskContext = runnableTask.getTaskContext();

        final ContextBuilder context = ContextBuilder.create()
            .authInfo( taskContext.getAuthInfo() )
            .branch( taskContext.getBranch() )
            .repositoryId( taskContext.getRepo() );

        if ( taskContext.getContentRootPath() != null )
        {

            context.attribute( "contentRootPath", taskContext.getContentRootPath() );
        }

        return context.build();
    }

    private void setThreadName()
    {
        final String defaultName = "task-" + id;
        final String threadName = defaultName.equalsIgnoreCase( runnableTask.getName() )
            ? "Task " + runnableTask.getApplicationKey() + "-" + id
            : "Task " + runnableTask.getName() + "-" + id;
        Thread.currentThread().setName( threadName );
    }
}
