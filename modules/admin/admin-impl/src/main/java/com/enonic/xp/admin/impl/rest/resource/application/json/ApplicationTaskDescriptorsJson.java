package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.task.TaskDescriptor;

public class ApplicationTaskDescriptorsJson
{
    private final ImmutableList<ApplicationTaskDescriptorJson> tasks;

    public ApplicationTaskDescriptorsJson( final Descriptors<TaskDescriptor> taskDescriptors )
    {
        final ImmutableList.Builder<ApplicationTaskDescriptorJson> builder = ImmutableList.builder();
        if(taskDescriptors != null)
        {
            for ( final TaskDescriptor taskDescriptor : taskDescriptors )
            {
                builder.add( new ApplicationTaskDescriptorJson( taskDescriptor ) );
            }
        }
        this.tasks = builder.build();
    }

    public ImmutableList<ApplicationTaskDescriptorJson> getTasks()
    {
        return tasks;
    }
}
