package com.enonic.xp.impl.task.event;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.event.Event;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;

public final class TaskEvents
{
    static final String TASK_SUBMITTED_EVENT = "task.submitted";

    static final String TASK_UPDATED_EVENT = "task.updated";

    static final String TASK_REMOVED_EVENT = "task.removed";

    static final String TASK_FINISHED_EVENT = "task.finished";

    static final String TASK_FAILED_EVENT = "task.failed";

    public static Event submitted( final TaskInfo task )
    {
        return task( TASK_SUBMITTED_EVENT, task );
    }

    public static Event updated( final TaskInfo task )
    {
        return task( TASK_UPDATED_EVENT, task );
    }

    public static Event finished( final TaskInfo task )
    {
        return task( TASK_FINISHED_EVENT, task );
    }

    public static Event failed( final TaskInfo task )
    {
        return task( TASK_FAILED_EVENT, task );
    }

    public static Event removed( final TaskInfo task )
    {
        return task( TASK_REMOVED_EVENT, task );
    }

    private static Event task( String type, final TaskInfo taskInfo )
    {
        if ( taskInfo == null )
        {
            return null;
        }

        final TaskProgress progress = taskInfo.getProgress();
        final ImmutableMap<Object, Object> taskProgressAsMap = ImmutableMap.builder().
            put( "info", progress.getInfo() ).
            put( "current", progress.getCurrent() ).
            put( "total", progress.getTotal() ).
            build();

        return Event.create( type ).
            distributed( true ).
            value( "description", taskInfo.getDescription() ).
            value( "id", taskInfo.getId().toString() ).
            value( "name", taskInfo.getName() ).
            value( "state", taskInfo.getState().toString() ).
            value( "progress", taskProgressAsMap ).
            value( "application", taskInfo.getApplication().toString() ).
            value( "user", taskInfo.getUser().toString() ).
            value( "startTime", taskInfo.getStartTime().toString() ).
            build();
    }
}
