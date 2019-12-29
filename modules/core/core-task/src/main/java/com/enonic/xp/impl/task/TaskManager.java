package com.enonic.xp.impl.task;

import java.util.List;

import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

public interface TaskManager
{
    TaskId submitTask( RunnableTask runnable, String description, String name );

    TaskInfo getTaskInfo( TaskId taskId );

    List<TaskInfo> getAllTasks();

    List<TaskInfo> getRunningTasks();
}
