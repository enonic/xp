package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.enonic.xp.task.TaskDescriptor;

public class ApplicationTaskDescriptorJson
{
    final String key;

    final String description;

    public ApplicationTaskDescriptorJson( final TaskDescriptor taskDescriptor )
    {
        this.key = taskDescriptor.getKey().toString();
        this.description = taskDescriptor.getDescription();
    }

    public String getKey()
    {
        return key;
    }

    public String getDescription()
    {
        return description;
    }
}
