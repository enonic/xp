package com.enonic.xp.lib.task;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

public final class ListTasksHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    public List<TaskMapper> list()
    {
        final TaskService taskService = taskServiceSupplier.get();
        final List<TaskInfo> tasks = taskService.getAllTasks();

        return tasks.stream().map( TaskMapper::new ).collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskServiceSupplier = context.getService( TaskService.class );
    }
}
