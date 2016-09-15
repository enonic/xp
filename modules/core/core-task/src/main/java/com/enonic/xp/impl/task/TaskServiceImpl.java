package com.enonic.xp.impl.task;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.impl.task.cluster.TaskTransportRequestSender;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

@Component(immediate = true)
public final class TaskServiceImpl
    implements TaskService
{
    private TaskManager taskManager;

    private TaskTransportRequestSender taskTransportRequestSender;

    @Override

    public TaskId submitTask( final RunnableTask runnable, final String description )
    {
        return taskManager.submitTask( runnable, description );
    }

    @Override
    public TaskInfo getTaskInfo( final TaskId taskId )
    {
        final List<TaskInfo> taskInfos = taskTransportRequestSender.getByTaskId( taskId );
        return taskInfos.isEmpty() ? null : taskInfos.get( 0 );
    }

    @Override
    public List<TaskInfo> getAllTasks()
    {
        return taskTransportRequestSender.getAllTasks();
    }

    @Override
    public List<TaskInfo> getRunningTasks()
    {
        return taskTransportRequestSender.getRunningTasks();
    }

    @Reference
    public void setTaskManager( final TaskManager taskManager )
    {
        this.taskManager = taskManager;
    }

    @Reference
    public void setTaskTransportRequestSender( final TaskTransportRequestSender taskTransportRequestSender )
    {
        this.taskTransportRequestSender = taskTransportRequestSender;
    }
}
