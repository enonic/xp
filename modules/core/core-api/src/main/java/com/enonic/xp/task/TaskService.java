package com.enonic.xp.task;

import java.util.List;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public interface TaskService
{
    TaskId submitTask( RunnableTask runnable, String description );

    TaskId submitTask( DescriptorKey key, PropertyTree config );

    TaskInfo getTaskInfo( TaskId taskId );

    List<TaskInfo> getAllTasks();

    List<TaskInfo> getRunningTasks();
}
