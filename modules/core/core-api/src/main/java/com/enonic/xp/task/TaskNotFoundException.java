package com.enonic.xp.task;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
public class TaskNotFoundException
    extends BaseException
{
    private final TaskKey task;

    public TaskNotFoundException( final TaskKey task )
    {
        super( "Task [{0}] not found", task );
        this.task = task;
    }

    public TaskNotFoundException( final TaskKey task, final String message )
    {
        super( "Task [{0}] not found. " + message, task );
        this.task = task;
    }

    public TaskKey getTask()
    {
        return task;
    }
}
