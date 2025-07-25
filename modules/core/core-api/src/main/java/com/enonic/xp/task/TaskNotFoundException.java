package com.enonic.xp.task;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.exception.BaseException;

@PublicApi
public class TaskNotFoundException
    extends BaseException
{
    private final DescriptorKey task;

    public TaskNotFoundException( final DescriptorKey task )
    {
        super( MessageFormat.format( "Task [{0}] not found", task ) );
        this.task = task;
    }

    public TaskNotFoundException( final DescriptorKey task, final String message )
    {
        super( MessageFormat.format( "Task [{0}] not found. {1}", task, message ) );
        this.task = task;
    }

    public DescriptorKey getTask()
    {
        return task;
    }
}
