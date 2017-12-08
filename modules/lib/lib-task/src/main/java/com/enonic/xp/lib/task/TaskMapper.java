package com.enonic.xp.lib.task;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;

public final class TaskMapper
    implements MapSerializable
{
    private final TaskInfo taskInfo;

    public TaskMapper( final TaskInfo taskInfo )
    {
        this.taskInfo = taskInfo;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "description", this.taskInfo.getDescription() );
        gen.value( "id", this.taskInfo.getId().toString() );
        gen.value( "name", this.taskInfo.getName() );
        gen.value( "state", this.taskInfo.getState().toString() );
        gen.value( "application", this.taskInfo.getApplication().toString() );
        gen.value( "user", this.taskInfo.getUser().toString() );
        gen.value( "startTime", this.taskInfo.getStartTime() );

        final TaskProgress progress = this.taskInfo.getProgress();
        gen.map( "progress" );
        gen.value( "info", progress.getInfo() );
        gen.value( "current", progress.getCurrent() );
        gen.value( "total", progress.getTotal() );
        gen.end();
    }

}
