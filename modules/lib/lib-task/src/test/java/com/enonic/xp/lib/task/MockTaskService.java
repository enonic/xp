package com.enonic.xp.lib.task;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskService;

class MockTaskService
    implements TaskService, ProgressReporter
{
    public TaskId taskId;

    public String description;

    public List<TaskProgress> progressHistory;

    @Override
    public TaskId submitTask( final RunnableTask runnable, final String description )
    {
        this.description = description;
        this.progressHistory = new ArrayList<>();
        runnable.run( taskId, this );
        return taskId;
    }

    @Override
    public TaskId submitTask( final DescriptorKey key )
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
}
