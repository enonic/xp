package com.enonic.wem.admin.json.content.page.image;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptors;


@SuppressWarnings("UnusedDeclaration")
public class ImageDescriptorsJson
{
    private final ImageDescriptors descriptors;

    private final List<ImageDescriptorJson> descriptorJsonList;

    public ImageDescriptorsJson( final ImageDescriptors descriptors )
    {
        this.descriptors = descriptors;
        ImmutableList.Builder<ImageDescriptorJson> builder = new ImmutableList.Builder<>();
        for ( ImageDescriptor descriptor : descriptors )
        {
            builder.add( new ImageDescriptorJson( descriptor ) );
        }
        this.descriptorJsonList = builder.build();
    }

    public List<ImageDescriptorJson> getDescriptors()
    {
        return descriptorJsonList;
    }
}
