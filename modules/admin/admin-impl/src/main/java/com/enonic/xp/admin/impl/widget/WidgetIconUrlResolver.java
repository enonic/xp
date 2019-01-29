package com.enonic.xp.admin.impl.widget;

import com.enonic.xp.admin.impl.rest.resource.schema.IconUrlResolver;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.icon.Icon;

public class WidgetIconUrlResolver
    extends IconUrlResolver
{
    private static final String REST_SCHEMA_ICON_URL = "/admin/rest/application/icon/";

    public WidgetIconUrlResolver()
    {
    }

    public String resolve( final WidgetDescriptor widgetDescriptor )
    {
        if ( widgetDescriptor != null && widgetDescriptor.getIcon() != null )
        {
            final String baseUrl = REST_SCHEMA_ICON_URL + widgetDescriptor.getApplicationKey().toString();
            final Icon icon = widgetDescriptor.getIcon();
            return generateIconUrl( baseUrl, icon );
        }

        return null;
    }
}
