package com.enonic.xp.impl.server.rest.model;

import java.util.Objects;

import com.enonic.xp.task.TaskProgress;

public class TaskProgressJson
{
    private final TaskProgress taskProgress;

    public TaskProgressJson( final TaskProgress taskProgress )
    {
        this.taskProgress = Objects.requireNonNull( taskProgress );
    }

    public int getCurrent()
    {
        return taskProgress.getCurrent();
    }

    public int getTotal()
    {
        return taskProgress.getTotal();
    }

    public String getInfo()
    {
        return taskProgress.getInfo();
    }
}
