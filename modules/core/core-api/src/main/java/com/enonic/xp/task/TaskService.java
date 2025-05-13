package com.enonic.xp.task;

import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface TaskService
{
    TaskId submitLocalTask( SubmitLocalTaskParams params );

    TaskId submitTask( SubmitTaskParams params );

    TaskInfo getTaskInfo( TaskId taskId );

    List<TaskInfo> getAllTasks();

    List<TaskInfo> getRunningTasks();
}
