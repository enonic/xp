package com.enonic.xp.lib.task;

import java.util.function.Function;
import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public final class ExecuteFunctionHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    private String description;

    private Function<Void, Void> taskFunction;

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public void setFunc( final Function<Void, Void> taskFunction )
    {
        this.taskFunction = taskFunction;
    }

    public String executeFunction()
    {
        final TaskService taskService = taskServiceSupplier.get();
        final TaskWrapper taskWrapper = new TaskWrapper( taskFunction, description );
        final TaskId taskId = taskService.submitTask( taskWrapper, description );

        return taskId.toString();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskServiceSupplier = context.getService( TaskService.class );
    }
}
