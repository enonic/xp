package com.enonic.xp.lib.task;

import java.util.List;
import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

public final class GetTasksHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    public TasksMapper getTasks()
    {
        final TaskService taskService = taskServiceSupplier.get();
        final List<TaskInfo> tasks = taskService.getAllTasks();

        return new TasksMapper( tasks );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskServiceSupplier = context.getService( TaskService.class );
    }
}
