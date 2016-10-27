package com.enonic.xp.impl.task.cluster;

import java.util.List;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

public interface TaskTransportRequestSender
{
    List<TaskInfo> getByTaskId( final TaskId taskId );

    List<TaskInfo> getRunningTasks();

    List<TaskInfo> getAllTasks();
}
