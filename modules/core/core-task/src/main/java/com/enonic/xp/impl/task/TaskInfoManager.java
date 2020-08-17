package com.enonic.xp.impl.task;

import java.util.List;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

public interface TaskInfoManager
{
    TaskInfo getTaskInfo( TaskId taskId );

    List<TaskInfo> getAllTasks();

    List<TaskInfo> getRunningTasks();
}
