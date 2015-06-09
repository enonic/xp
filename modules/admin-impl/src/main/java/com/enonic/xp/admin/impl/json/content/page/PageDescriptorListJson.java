package com.enonic.xp.admin.impl.json.content.page;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptors;

@SuppressWarnings("UnusedDeclaration")
public class PageDescriptorListJson
{
    private final ImmutableList<PageDescriptorJson> pageDescriptors;

    public PageDescriptorListJson( final PageDescriptors pageDescriptors )
    {
        final ImmutableList.Builder<PageDescriptorJson> builder = ImmutableList.builder();
        for ( final PageDescriptor pageDescriptor : pageDescriptors )
        {
            builder.add( new PageDescriptorJson( pageDescriptor ) );
        }
        this.pageDescriptors = builder.build();
    }

    public ImmutableList<PageDescriptorJson> getDescriptors()
    {
        return pageDescriptors;
    }
}
