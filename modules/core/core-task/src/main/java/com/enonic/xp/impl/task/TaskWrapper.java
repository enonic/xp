package com.enonic.xp.impl.task;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskState;

final class TaskWrapper
    implements Runnable, ProgressReporter
{
    private final TaskId taskId;

    private final RunnableTask runnableTask;

    private final TaskManagerImpl taskManager;

    private final Branch branch;

    private final RepositoryId repo;

    private final AuthenticationInfo authInfo;

    public TaskWrapper( final TaskId taskId, final RunnableTask runnableTask, final Context userContext, final TaskManagerImpl taskManager )
    {
        this.taskId = taskId;
        this.runnableTask = runnableTask;
        this.taskManager = taskManager;
        this.branch = userContext.getBranch();
        this.repo = userContext.getRepositoryId();
        this.authInfo = userContext.getAuthInfo();
    }

    @Override
    public void run()
    {
        taskManager.updateState( taskId, TaskState.RUNNING );
        try
        {
            callTaskWithContext();
            taskManager.updateState( taskId, TaskState.FINISHED );
        }
        catch ( Throwable t )
        {
            taskManager.updateState( taskId, TaskState.FAILED );
        }
    }

    private void callTaskWithContext()
    {
        getContext().callWith( () ->
                               {
                                   runnableTask.run( taskId, this );
                                   return null;
                               } );
    }

    private Context getContext()
    {
        return ContextBuilder.create().authInfo( authInfo ).branch( branch ).repositoryId( repo ).build();
    }

    @Override
    public void progress( final int current, final int total )
    {
        taskManager.updateProgress( taskId, current, total );
    }

    @Override
    public void info( final String message )
    {
        taskManager.updateProgress( taskId, message );
    }
}
