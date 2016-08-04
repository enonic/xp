package com.enonic.xp.admin.impl.rest.resource.application;

import com.enonic.xp.admin.impl.rest.resource.schema.IconUrlResolver;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;

public final class ApplicationIconUrlResolver
    extends IconUrlResolver
{
    private static final String REST_SCHEMA_ICON_URL = "/admin/rest/application/icon/";

    public ApplicationIconUrlResolver()
    {
    }

    public String resolve( final ApplicationKey applicationKey, final ApplicationDescriptor applicationDescriptor )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + applicationKey.toString();
        final Icon icon = applicationDescriptor == null ? null : applicationDescriptor.getIcon();
        return generateIconUrl( baseUrl, icon );
    }
}
