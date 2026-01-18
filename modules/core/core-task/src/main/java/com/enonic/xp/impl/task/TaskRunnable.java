package com.enonic.xp.impl.task;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.impl.task.distributed.DescribedTask;
import com.enonic.xp.impl.task.distributed.TaskContext;
import com.enonic.xp.security.User;
import com.enonic.xp.trace.Tracer;

import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE;

final class TaskRunnable
    implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger( TaskRunnable.class );

    private final DescribedTask runnableTask;

    private final InternalProgressReporter progressReporter;

    TaskRunnable( final DescribedTask runnableTask, final InternalProgressReporter progressReporter )
    {
        this.runnableTask = runnableTask;
        this.progressReporter = progressReporter;
    }

    @Override
    public void run()
    {
        final String originalThreadName = Thread.currentThread().getName();
        Thread.currentThread().setName( betterThreadName() );
        try
        {
            User user = runnableTask.getTaskContext().getAuthInfo() != null ? runnableTask.getTaskContext().getAuthInfo().getUser() : null;
            Tracer.trace( "task.run", trace -> {
                trace.put( "taskId", runnableTask.getTaskId() );
                trace.put( "user", Objects.requireNonNullElseGet( user, User::anonymous ).getKey() );
                trace.put( "app", runnableTask.getApplicationKey() );
            }, this::doRun, ( trace, success ) -> trace.put( "success", success ) );
        }
        finally
        {
            Thread.currentThread().setName( originalThreadName );
        }
    }

    private boolean doRun()
    {
        progressReporter.running();
        try
        {
            newContext().runWith( () -> runnableTask.run( progressReporter ) );
            progressReporter.finished();
            return true;
        }
        catch ( Throwable t )
        {
            progressReporter.failed( t.getMessage() );
            LOG.error( "Error executing task [{}] '{}': {}", runnableTask.getTaskId(), runnableTask.getName(), t.getMessage(), t );
            return false;
        }
    }

    private Context newContext()
    {
        final TaskContext taskContext = runnableTask.getTaskContext();

        final ContextBuilder context = ContextBuilder.create();

        if ( taskContext.getAuthInfo() != null )
        {
            context.authInfo( taskContext.getAuthInfo() );
        }
        if ( taskContext.getBranch() != null )
        {
            context.branch( taskContext.getBranch() );
        }
        if ( taskContext.getRepo() != null )
        {
            context.repositoryId( taskContext.getRepo() );
        }
        if ( taskContext.getContentRootPath() != null )
        {
            context.attribute( CONTENT_ROOT_PATH_ATTRIBUTE, taskContext.getContentRootPath() );
        }

        return context.build();
    }

    private String betterThreadName()
    {
        final String defaultName = "task-" + runnableTask.getApplicationKey() + "-" + runnableTask.getTaskId();
        return defaultName.equals( runnableTask.getName() )
            ? defaultName
            : "task-" + runnableTask.getName() + "-" + runnableTask.getTaskId();
    }
}
