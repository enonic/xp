package com.enonic.xp.impl.task;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskService;

public class MockTaskService
    implements TaskService, ProgressReporter
{
    public TaskId taskId;

    public String description;

    public List<TaskProgress> progressHistory;

    @Override
    public TaskId submitLocalTask( final SubmitLocalTaskParams params )
    {
        this.description = params.getDescription();
        this.progressHistory = new ArrayList<>();
        params.getRunnableTask().run( taskId, this );
        return taskId;
    }

    @Override
    public TaskId submitTask( final SubmitTaskParams params )
    {
        return taskId;
    }

    @Override
    public TaskInfo getTaskInfo( final TaskId taskId )
    {
        return null;
    }

    @Override
    public List<TaskInfo> getAllTasks()
    {
        return null;
    }

    @Override
    public List<TaskInfo> getRunningTasks()
    {
        return null;
    }

    @Override
    public void progress( final int current, final int total )
    {
        progressHistory.add( TaskProgress.create().current( current ).total( total ).build() );
    }

    @Override
    public void info( final String message )
    {
        progressHistory.add( TaskProgress.create().info( message ).build() );
    }

    @Override
    public void progress( final Integer current, final Integer total, final String info )
    {
        final TaskProgress.Builder builder = TaskProgress.create();
        if ( current != null )
        {
            builder.current( current );
        }
        if ( total != null )
        {
            builder.total( total );
        }
        if ( info != null )
        {
            builder.info( info );
        }

        progressHistory.add( builder.build() );
    }

    @Override
    public void progress( final ProgressReportParams params )
    {
        final TaskProgress.Builder builder = TaskProgress.create();
        if ( params.getCurrent() != null )
        {
            builder.current( params.getCurrent() );
        }
        if ( params.getTotal() != null )
        {
            builder.total( params.getTotal() );
        }
        if ( params.getInfo() != null )
        {
            builder.info( params.getInfo() );
        }

        progressHistory.add( builder.build() );
    }
}
