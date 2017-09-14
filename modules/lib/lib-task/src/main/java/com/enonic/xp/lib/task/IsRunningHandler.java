package com.enonic.xp.lib.task;

import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskService;

public final class IsRunningHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    public boolean isRunning( final String taskNameOrId )
    {
        final TaskService taskService = this.taskServiceSupplier.get();

        return taskService.getRunningTasks().stream().
            anyMatch( ( t ) -> t.getName().equals( taskNameOrId ) || t.getId().toString().equals( taskNameOrId ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskServiceSupplier = context.getService( TaskService.class );
    }
}
