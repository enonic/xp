package com.enonic.xp.task;

import java.util.List;

import com.google.common.annotations.Beta;

import com.enonic.xp.page.DescriptorKey;

@Beta
public interface TaskService
{
    TaskId submitTask( RunnableTask runnable, String description );

    TaskId submitTask( DescriptorKey key );

    TaskInfo getTaskInfo( TaskId taskId );

    List<TaskInfo> getAllTasks();

    List<TaskInfo> getRunningTasks();
}
