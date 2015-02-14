package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.core.content.page.region.LayoutDescriptor;
import com.enonic.xp.core.content.page.region.LayoutDescriptors;


@SuppressWarnings("UnusedDeclaration")
public class LayoutDescriptorsJson
{
    private final LayoutDescriptors descriptors;

    private final List<LayoutDescriptorJson> descriptorJsonList;

    public LayoutDescriptorsJson( final LayoutDescriptors descriptors )
    {
        this.descriptors = descriptors;
        ImmutableList.Builder<LayoutDescriptorJson> builder = new ImmutableList.Builder<>();
        for ( LayoutDescriptor descriptor : descriptors )
        {
            builder.add( new LayoutDescriptorJson( descriptor ) );
        }
        this.descriptorJsonList = builder.build();
    }

    public List<LayoutDescriptorJson> getDescriptors()
    {
        return descriptorJsonList;
    }
}
