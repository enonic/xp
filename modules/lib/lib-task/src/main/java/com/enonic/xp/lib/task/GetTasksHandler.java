package com.enonic.xp.lib.task;

import java.util.List;
import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskManager;

public final class GetTasksHandler
    implements ScriptBean
{
    private Supplier<TaskManager> taskManager;

    public TasksMapper getTasks()
    {
        final TaskManager taskMan = taskManager.get();
        final List<TaskInfo> tasks = taskMan.getAllTasks();

        return new TasksMapper( tasks );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskManager = context.getService( TaskManager.class );
    }
}
