package com.enonic.xp.impl.task.distributed;

import java.util.List;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

public interface TaskManager
{
    TaskInfo getTaskInfo( TaskId taskId );

    List<TaskInfo> getAllTasks();

    List<TaskInfo> getRunningTasks();

    void submitTask( DescribedTask task );
}
