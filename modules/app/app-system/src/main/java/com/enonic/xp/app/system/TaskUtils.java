package com.enonic.xp.app.system;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

public final class TaskUtils
{
    private TaskUtils()
    {
    }

    public static void checkAlreadySubmitted( final TaskInfo currentTaskInfo, final Collection<TaskInfo> allTasks )
    {
        final TaskId currentTaskId = currentTaskInfo.getId();
        final String currentTaskName = currentTaskInfo.getName();
        final Instant currentTaskStartTime = currentTaskInfo.getStartTime();

        final Optional<TaskInfo> priorTask = allTasks.stream()
            .filter( ti -> ti.getId() != currentTaskId )
            .filter( ti -> currentTaskName.equals( ti.getName() ) )
            .filter( ti -> !ti.isDone() )
            .filter( ti -> ti.getStartTime().isBefore( currentTaskStartTime ) ||
                ti.getStartTime().equals( currentTaskStartTime ) && ti.getId().toString().compareTo( currentTaskId.toString() ) < 0 )
            .findAny();

        if ( priorTask.isPresent() )
        {
            throw new IllegalStateException( "Task " + currentTaskName + " [" + priorTask.get().getId() + "] is already submitted" );
        }
    }

}
