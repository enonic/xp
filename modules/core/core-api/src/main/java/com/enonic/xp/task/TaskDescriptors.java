package com.enonic.xp.task;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class TaskDescriptors
    extends AbstractImmutableEntityList<TaskDescriptor>
{
    private TaskDescriptors( final ImmutableList<TaskDescriptor> list )
    {
        super( list );
    }

    public static TaskDescriptors empty()
    {
        final ImmutableList<TaskDescriptor> list = ImmutableList.of();
        return new TaskDescriptors( list );
    }

    public static TaskDescriptors from( final TaskDescriptor... descriptors )
    {
        return from( ImmutableList.copyOf( descriptors ) );
    }

    public static TaskDescriptors from( final Iterable<TaskDescriptor> descriptors )
    {
        return new TaskDescriptors( ImmutableList.copyOf( descriptors ) );
    }
}
