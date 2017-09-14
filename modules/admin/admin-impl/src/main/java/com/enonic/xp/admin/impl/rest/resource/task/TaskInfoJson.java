package com.enonic.xp.admin.impl.rest.resource.task;

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

    public TaskProgressJson getProgress()
    {
        return taskInfo != null && taskInfo.getProgress() != null ? new TaskProgressJson( taskInfo.getProgress() ) : null;
    }
}
