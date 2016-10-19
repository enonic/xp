package com.enonic.xp.lib.task;

import java.util.List;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.task.TaskInfo;

public final class TasksMapper
    implements MapSerializable
{
    private final List<TaskInfo> tasks;

    public TasksMapper( final List<TaskInfo> tasks )
    {
        this.tasks = tasks;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "tasks" );
        for ( final TaskInfo task : tasks )
        {
            gen.map();
            new TaskMapper( task ).serialize( gen );
            gen.end();
        }
        gen.end();
    }

}
