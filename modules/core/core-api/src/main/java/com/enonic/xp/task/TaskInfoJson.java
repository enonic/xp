package com.enonic.xp.task;

import java.time.Instant;
import java.util.Objects;

@Deprecated
public class TaskInfoJson
{
    private final TaskInfo taskInfo;

    public TaskInfoJson( final TaskInfo taskInfo )
    {
        this.taskInfo = Objects.requireNonNull( taskInfo );
    }

    public String getId()
    {
        return taskInfo.getId().toString();
    }

    public String getDescription()
    {
        return taskInfo.getDescription();
    }

    public String getName()
    {
        return taskInfo.getName();
    }

    public String getState()
    {
        return taskInfo.getState().name();
    }

    public String getApplication()
    {
        return taskInfo.getApplication().toString();
    }

    public String getUser()
    {
        return taskInfo.getUser().toString();
    }

    public Instant getStartTime()
    {
        return taskInfo.getStartTime();
    }

    public TaskProgressJson getProgress()
    {
        return taskInfo.getProgress() != null ? new TaskProgressJson( taskInfo.getProgress() ) : null;
    }
}
