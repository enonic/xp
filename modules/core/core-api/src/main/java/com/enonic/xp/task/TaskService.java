package com.enonic.xp.task;

import java.util.List;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public interface TaskService
{
    TaskId submitLocalTask( SubmitLocalTaskParams params );

    @Deprecated
    TaskId submitTask( DescriptorKey key, PropertyTree config );

    TaskId submitTask( SubmitTaskParams params );

    TaskInfo getTaskInfo( TaskId taskId );

    List<TaskInfo> getAllTasks();

    List<TaskInfo> getRunningTasks();
}
