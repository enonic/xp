package com.enonic.xp.admin.impl.rest.resource.widget.json;

import java.util.List;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.descriptor.Descriptors;

import static java.util.stream.Collectors.toList;


@SuppressWarnings("UnusedDeclaration")
public class WidgetDescriptorsJson
{
    private final List<WidgetDescriptorJson> descriptorJsonList;

    public WidgetDescriptorsJson( final List<WidgetDescriptorJson> descriptorJsonList )
    {
        this.descriptorJsonList = descriptorJsonList;
    }

    public WidgetDescriptorsJson( final Descriptors<WidgetDescriptor> descriptors )
    {
        this.descriptorJsonList = descriptors.stream().map( WidgetDescriptorJson::new ).collect( toList() );
    }

    public List<WidgetDescriptorJson> getDescriptors()
    {
        return descriptorJsonList;
    }
}
