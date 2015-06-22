package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptors;


@SuppressWarnings("UnusedDeclaration")
public class PartDescriptorsJson
{
    private final PartDescriptors descriptors;

    private final List<PartDescriptorJson> descriptorJsonList;

    public PartDescriptorsJson( final PartDescriptors descriptors )
    {
        this.descriptors = descriptors;
        ImmutableList.Builder<PartDescriptorJson> builder = new ImmutableList.Builder<>();
        for ( PartDescriptor descriptor : descriptors )
        {
            builder.add( new PartDescriptorJson( descriptor ) );
        }
        this.descriptorJsonList = builder.build();
    }

    public List<PartDescriptorJson> getDescriptors()
    {
        return descriptorJsonList;
    }
}
