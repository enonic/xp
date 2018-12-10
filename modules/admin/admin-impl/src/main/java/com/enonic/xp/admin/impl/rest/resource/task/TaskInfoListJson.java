package com.enonic.xp.admin.impl.rest.resource.task;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskInfoJson;

public class TaskInfoListJson
{
    private List<TaskInfoJson> tasks;

    public TaskInfoListJson( final List<TaskInfo> taskInfoList )
    {
        tasks = taskInfoList.stream().map( TaskInfoJson::new ).collect( Collectors.toList() );
    }

    public List<TaskInfoJson> getTasks()
    {
        return tasks;
    }
}
