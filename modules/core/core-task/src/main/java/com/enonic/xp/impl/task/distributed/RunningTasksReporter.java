package com.enonic.xp.impl.task.distributed;

import java.util.List;

import com.enonic.xp.task.TaskInfo;

public final class RunningTasksReporter
    implements SerializableFunction<TaskManager, List<TaskInfo>>
{
    private static final long serialVersionUID = 0;

    @Override
    public List<TaskInfo> apply( final TaskManager taskManager )
    {
        return List.copyOf( taskManager.getRunningTasks() );
    }
}
