package com.enonic.xp.task;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.BaseException;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public class TaskNotFoundException
    extends BaseException
{
    private final DescriptorKey task;

    public TaskNotFoundException( final DescriptorKey task )
    {
        super( "Task [{0}] not found", task );
        this.task = task;
    }

    public TaskNotFoundException( final DescriptorKey task, final String message )
    {
        super( "Task [{0}] not found. " + message, task );
        this.task = task;
    }

    public DescriptorKey getTask()
    {
        return task;
    }
}
