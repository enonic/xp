package com.enonic.xp.lib.task;

import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

public final class GetTaskHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    private String taskId;

    public void setTaskId( final String taskId )
    {
        this.taskId = taskId;
    }

    public TaskMapper getTask()
    {
        final TaskService taskService = this.taskServiceSupplier.get();
        final TaskInfo taskInfo = taskService.getTaskInfo( TaskId.from( taskId ) );

        return taskInfo == null ? null : new TaskMapper( taskInfo );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskServiceSupplier = context.getService( TaskService.class );
    }
}
