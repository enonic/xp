package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptors;


@SuppressWarnings("UnusedDeclaration")
public class LayoutDescriptorsJson
{
    private final List<LayoutDescriptorJson> descriptorJsonList;

    public LayoutDescriptorsJson( final List<LayoutDescriptorJson> layoutDescriptorJsons )
    {
        this.descriptorJsonList = layoutDescriptorJsons;
    }

    public LayoutDescriptorsJson( final LayoutDescriptors descriptors, final LocaleMessageResolver localeMessageResolver )
    {
        ImmutableList.Builder<LayoutDescriptorJson> builder = new ImmutableList.Builder<>();
        if ( descriptors != null )
        {
            for ( LayoutDescriptor descriptor : descriptors )
            {
                builder.add( new LayoutDescriptorJson( descriptor, localeMessageResolver ) );
            }
        }
        this.descriptorJsonList = builder.build();
    }

    public List<LayoutDescriptorJson> getDescriptors()
    {
        return descriptorJsonList;
    }
}
