package com.enonic.xp.lib.task;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.task.TaskState;

public final class ListTasksHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    private String name;

    private TaskState state;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setState( final String state )
    {
        this.state = state == null ? null : TaskState.valueOf( state.toUpperCase() );
    }

    public List<TaskMapper> list()
    {
        final TaskService taskService = taskServiceSupplier.get();
        final List<TaskInfo> tasks = taskService.getAllTasks();

        Stream<TaskInfo> taskStream = tasks.stream();
        if ( name != null )
        {
            taskStream = taskStream.filter( ( t ) -> t.getName().equals( name ) );
        }
        if ( state != null )
        {
            taskStream = taskStream.filter( ( t ) -> t.getState() == state );
        }

        return taskStream.map( TaskMapper::new ).collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskServiceSupplier = context.getService( TaskService.class );
    }
}
