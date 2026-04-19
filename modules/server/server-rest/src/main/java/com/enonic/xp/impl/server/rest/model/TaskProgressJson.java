package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.task.TaskProgress;

import static java.util.Objects.requireNonNull;

public class TaskProgressJson
{
    private final TaskProgress taskProgress;

    public TaskProgressJson( final TaskProgress taskProgress )
    {
        this.taskProgress = requireNonNull( taskProgress );
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
