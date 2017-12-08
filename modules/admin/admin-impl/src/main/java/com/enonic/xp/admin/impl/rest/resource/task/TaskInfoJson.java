package com.enonic.xp.admin.impl.rest.resource.task;

import java.time.Instant;

import com.enonic.xp.task.TaskInfo;

public class TaskInfoJson
{
    private final TaskInfo taskInfo;

    public TaskInfoJson( final TaskInfo taskInfo )
    {
        this.taskInfo = taskInfo;
    }

    public String getId()
    {
        return taskInfo != null ? taskInfo.getId().toString() : "";
    }

    public String getDescription()
    {
        return taskInfo != null ? taskInfo.getDescription() : "";
    }

    public String getName()
    {
        return taskInfo != null ? taskInfo.getName() : "";
    }

    public String getState()
    {
        return taskInfo != null ? taskInfo.getState().name() : "";
    }

    public String getApplication()
    {
        return taskInfo != null ? taskInfo.getApplication().toString() : "";
    }

    public String getUser()
    {
        return taskInfo != null ? taskInfo.getUser().toString() : "";
    }

    public Instant getStartTime()
    {
        return taskInfo != null ? taskInfo.getStartTime() : null;
    }

    public TaskProgressJson getProgress()
    {
        return taskInfo != null && taskInfo.getProgress() != null ? new TaskProgressJson( taskInfo.getProgress() ) : null;
    }
}
