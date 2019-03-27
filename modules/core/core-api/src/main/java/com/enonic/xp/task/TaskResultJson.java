package com.enonic.xp.task;

public class TaskResultJson
{
    private final TaskId taskId;

    public TaskResultJson( final TaskId taskId )
    {
        this.taskId = taskId;
    }

    public String getTaskId()
    {
        return taskId.toString();
    }
}
