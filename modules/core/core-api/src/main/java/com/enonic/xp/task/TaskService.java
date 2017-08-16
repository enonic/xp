package com.enonic.xp.task;

import java.util.List;

import com.google.common.annotations.Beta;

@Beta
public interface TaskService
{
    TaskId submitTask( RunnableTask runnable, String description );

    TaskId submitTask( TaskKey key );

    TaskInfo getTaskInfo( TaskId taskId );

    List<TaskInfo> getAllTasks();

    List<TaskInfo> getRunningTasks();
}
