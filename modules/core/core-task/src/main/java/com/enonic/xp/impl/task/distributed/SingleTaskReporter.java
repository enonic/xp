package com.enonic.xp.impl.task.distributed;

import java.util.List;
import java.util.Optional;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

public final class SingleTaskReporter
    implements SerializableFunction<TaskManager, List<TaskInfo>>
{
    private static final long serialVersionUID = 0;

    private final TaskId taskId;

    public SingleTaskReporter( final TaskId taskId )
    {
        this.taskId = taskId;
    }

    @Override
    public List<TaskInfo> apply( final TaskManager taskManager )
    {
        return Optional.ofNullable( taskManager.getTaskInfo( taskId ) ).map( List::of ).orElse( List.of() );
    }
}
