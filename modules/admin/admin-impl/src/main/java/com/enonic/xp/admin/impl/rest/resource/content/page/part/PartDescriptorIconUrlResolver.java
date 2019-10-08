package com.enonic.xp.admin.impl.rest.resource.content.page.part;

import com.enonic.xp.admin.impl.rest.resource.schema.IconUrlResolver;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.region.PartDescriptor;


public final class PartDescriptorIconUrlResolver
    extends IconUrlResolver
{
    public static final String DESCRIPTOR_ICON = "/admin/rest/content/page/part/descriptor/icon/";

    public String resolve( final PartDescriptor partDescriptor )
    {
        final String baseUrl = DESCRIPTOR_ICON + partDescriptor.getKey().toString();
        final Icon icon = partDescriptor.getIcon();
        return generateIconUrl( baseUrl, icon );
    }

}
