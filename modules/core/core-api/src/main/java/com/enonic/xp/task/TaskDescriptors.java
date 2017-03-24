package com.enonic.xp.task;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public class TaskDescriptors
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

    public static TaskDescriptors from( final TaskDescriptor... TaskDescriptors )
    {
        return new TaskDescriptors( ImmutableList.copyOf( TaskDescriptors ) );
    }

    public static TaskDescriptors from( final Iterable<? extends TaskDescriptor> TaskDescriptors )
    {
        return new TaskDescriptors( ImmutableList.copyOf( TaskDescriptors ) );
    }

    public static TaskDescriptors from( final Collection<? extends TaskDescriptor> TaskDescriptors )
    {
        return new TaskDescriptors( ImmutableList.copyOf( TaskDescriptors ) );
    }
}
