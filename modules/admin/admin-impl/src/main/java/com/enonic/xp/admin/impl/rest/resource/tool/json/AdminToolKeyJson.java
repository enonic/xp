package com.enonic.xp.admin.impl.rest.resource.tool.json;

import com.enonic.xp.page.DescriptorKey;

public class AdminToolKeyJson
{
    private final DescriptorKey descriptorKey;

    public AdminToolKeyJson( final DescriptorKey descriptorKey )
    {
        this.descriptorKey = descriptorKey;
    }

    public String getApplication()
    {
        return descriptorKey.getApplicationKey().getName();
    }

    public String getName()
    {
        return descriptorKey.getName();
    }
}
