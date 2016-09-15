package com.enonic.xp.impl.task;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.impl.task.cluster.GetAllTasksRequestSender;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

@Component(immediate = true)
public final class TaskServiceImpl
    implements TaskService
{
    private TaskManager taskManager;

    private GetAllTasksRequestSender getAllTasksRequestSender;

    @Override

    public TaskId submitTask( final RunnableTask runnable, final String description )
    {
        return taskManager.submitTask( runnable, description );
    }

    @Override
    public TaskInfo getTaskInfo( final TaskId taskId )
    {
        //TODO Optimize by making a generic request with different types: all, running, byId
        return getAllTasksRequestSender.getAllTasks().
            stream().
            filter( TaskInfo::isRunning ).
            findFirst().
            orElse( null );
    }

    @Override
    public List<TaskInfo> getAllTasks()
    {
        return getAllTasksRequestSender.getAllTasks();
    }

    @Override
    public List<TaskInfo> getRunningTasks()
    {
        //TODO Optimize by making a generic request with different types: all, running, byId
        final ImmutableList.Builder<TaskInfo> runningTasks = ImmutableList.builder();
        getAllTasksRequestSender.getAllTasks().
            stream().
            filter( TaskInfo::isRunning ).
            forEach( runningTasks::add );
        return runningTasks.build();
    }


    @Reference
    public void setTaskManager( final TaskManager taskManager )
    {
        this.taskManager = taskManager;
    }
}
