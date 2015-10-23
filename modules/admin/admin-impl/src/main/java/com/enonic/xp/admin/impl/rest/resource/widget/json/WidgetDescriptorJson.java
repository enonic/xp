package com.enonic.xp.admin.impl.rest.resource.widget.json;


import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.admin.widget.WidgetDescriptor;

public final class WidgetDescriptorJson
{

    private final String key;

    private final String displayName;

    private final String url;

    private final Set<String> interfaces;

    public WidgetDescriptorJson( final WidgetDescriptor widgetDescriptor )
    {
        this.key = widgetDescriptor.getKeyString();
        this.displayName = widgetDescriptor.getDisplayName();
        this.url = widgetDescriptor.getUrl();
        this.interfaces = ImmutableSet.copyOf( widgetDescriptor.getInterfaces() );
    }

    public String getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getUrl()
    {
        return url;
    }

    public Set<String> getInterfaces()
    {
        return interfaces;
    }
}
