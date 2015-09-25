package com.enonic.xp.admin.impl.rest.resource.widget.json;


import java.util.List;

import com.enonic.xp.widget.WidgetDescriptor;
import com.enonic.xp.widget.WidgetDescriptors;

public final class WidgetDescriptorsJson
{

    private List<WidgetDescriptor> widgetDescriptors;

    public WidgetDescriptorsJson( final WidgetDescriptors widgetDescriptors )
    {
        if ( widgetDescriptors != null )
        {
            this.widgetDescriptors = widgetDescriptors.getList();
        }
    }

    public List<WidgetDescriptor> getWidgetDescriptors()
    {
        return widgetDescriptors;
    }
}
