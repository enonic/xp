package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptors;


@SuppressWarnings("UnusedDeclaration")
public class PartDescriptorsJson
{
    private final List<PartDescriptorJson> descriptorJsonList;

    public PartDescriptorsJson( final List<PartDescriptorJson> descriptorJsonList )
    {
        this.descriptorJsonList = descriptorJsonList;
    }

    public PartDescriptorsJson( final PartDescriptors descriptors, final LocaleMessageResolver localeMessageResolver,
                                final InlineMixinResolver inlineMixinResolver )
    {
        ImmutableList.Builder<PartDescriptorJson> builder = new ImmutableList.Builder<>();
        if ( descriptors != null )
        {
            for ( PartDescriptor descriptor : descriptors )
            {
                builder.add( new PartDescriptorJson( descriptor, localeMessageResolver, inlineMixinResolver ) );
            }
        }
        this.descriptorJsonList = builder.build();
    }

    public List<PartDescriptorJson> getDescriptors()
    {
        return descriptorJsonList;
    }
}
