package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.region.PartDescriptors;


@SuppressWarnings("UnusedDeclaration")
public class PartDescriptorsJson
{
    private final List<PartDescriptorJson> descriptorJsonList;

    public PartDescriptorsJson( final List<PartDescriptorJson> descriptorJsonList )
    {
        this.descriptorJsonList = List.copyOf( descriptorJsonList );
    }

    public PartDescriptorsJson( final PartDescriptors descriptors, final LocaleMessageResolver localeMessageResolver,
                                final InlineMixinResolver inlineMixinResolver )
    {
        this.descriptorJsonList = descriptors.stream()
            .map( descriptor -> new PartDescriptorJson( descriptor, localeMessageResolver, inlineMixinResolver ) )
            .collect( Collectors.toUnmodifiableList() );
    }

    public List<PartDescriptorJson> getDescriptors()
    {
        return descriptorJsonList;
    }
}
