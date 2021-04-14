package com.enonic.xp.admin.impl.json.content.page;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.page.PageDescriptors;

@SuppressWarnings("UnusedDeclaration")
public class PageDescriptorListJson
{
    private final List<PageDescriptorJson> pageDescriptors;

    public PageDescriptorListJson( final List<PageDescriptorJson> pageDescriptors )
    {
        this.pageDescriptors = List.copyOf( pageDescriptors );
    }

    public PageDescriptorListJson( final PageDescriptors pageDescriptors, final LocaleMessageResolver localeMessageResolver,
                                   final InlineMixinResolver inlineMixinResolver )
    {
        this.pageDescriptors = pageDescriptors.stream()
            .map( pageDescriptor -> new PageDescriptorJson( pageDescriptor, localeMessageResolver, inlineMixinResolver ) )
            .collect( Collectors.toUnmodifiableList() );
    }

    public List<PageDescriptorJson> getDescriptors()
    {
        return pageDescriptors;
    }
}
