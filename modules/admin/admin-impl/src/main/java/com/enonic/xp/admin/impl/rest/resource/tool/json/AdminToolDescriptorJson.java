package com.enonic.xp.admin.impl.rest.resource.tool.json;

import com.enonic.xp.admin.tool.AdminToolDescriptor;

public final class AdminToolDescriptorJson
{
    public String key;

    public String application;

    public String name;

    public String displayName;

    public String description;

    public String icon;

    public String toolUrl;

    public AdminToolDescriptorJson( final AdminToolDescriptor adminToolDescriptor, final String iconPath, final String toolUrl )
    {
        this.key = adminToolDescriptor.getKey().toString();
        this.application = adminToolDescriptor.getApplicationKey().toString();
        this.name = adminToolDescriptor.getName();
        this.displayName = adminToolDescriptor.getDisplayName();
        this.description = adminToolDescriptor.getDescription();
        this.icon = iconPath;
        this.toolUrl = toolUrl;
    }
}
