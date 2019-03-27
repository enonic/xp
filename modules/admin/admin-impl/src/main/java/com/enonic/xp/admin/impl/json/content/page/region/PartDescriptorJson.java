package com.enonic.xp.admin.impl.json.content.page.region;

import com.enonic.xp.admin.impl.json.content.page.DescriptorJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.region.PartDescriptor;

public class PartDescriptorJson
    extends DescriptorJson
{
    public PartDescriptorJson( final PartDescriptor descriptor, final LocaleMessageResolver localeMessageResolver,
                               final InlineMixinResolver inlineMixinResolver )
    {
        super( descriptor, localeMessageResolver, inlineMixinResolver );
    }
}
