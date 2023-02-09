package com.enonic.xp.task;

import java.util.Objects;

@Deprecated
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
