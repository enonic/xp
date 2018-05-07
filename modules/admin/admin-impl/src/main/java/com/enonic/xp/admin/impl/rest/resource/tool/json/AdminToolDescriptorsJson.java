package com.enonic.xp.admin.impl.rest.resource.tool.json;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class AdminToolDescriptorsJson
{
    private final List<AdminToolDescriptorJson> descriptorJsonList;

    public AdminToolDescriptorsJson( final List<AdminToolDescriptorJson> descriptorJsonList )
    {
        this.descriptorJsonList = descriptorJsonList;
    }

    public List<AdminToolDescriptorJson> getDescriptors()
    {
        return descriptorJsonList;
    }
}
