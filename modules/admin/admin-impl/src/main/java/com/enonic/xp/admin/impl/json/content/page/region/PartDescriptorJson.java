package com.enonic.xp.admin.impl.json.content.page.region;

import com.enonic.xp.admin.impl.json.content.page.DescriptorJson;
import com.enonic.xp.admin.impl.rest.resource.content.page.part.PartDescriptorIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.region.PartDescriptor;

public class PartDescriptorJson
    extends DescriptorJson
{
    private String icon;

    public PartDescriptorJson( final PartDescriptor descriptor, final LocaleMessageResolver localeMessageResolver,
                               final InlineMixinResolver inlineMixinResolver,
                               final PartDescriptorIconUrlResolver partDescriptorIconUrlResolver )
    {
        super( descriptor, localeMessageResolver, inlineMixinResolver );
        this.icon = partDescriptorIconUrlResolver.resolve( descriptor );
    }

    public String getIcon()
    {
        return icon;
    }
}
