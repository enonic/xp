package com.enonic.xp.lib.task;

import java.util.function.Function;
import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskManager;

public final class SubmitTaskHandler
    implements ScriptBean
{
    private Supplier<TaskManager> taskManager;

    private String description;

    private Function<Void, Void> taskFunction;

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public void setTask( final Function<Void, Void> taskFunction )
    {
        this.taskFunction = taskFunction;
    }

    public String submit()
    {
        final TaskManager taskMan = taskManager.get();
        final TaskWrapper taskWrapper = new TaskWrapper( taskFunction, description );
        final TaskId taskId = taskMan.submitTask( taskWrapper, description );

        return taskId.toString();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskManager = context.getService( TaskManager.class );
    }
}
