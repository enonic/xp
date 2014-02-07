package com.enonic.wem.admin.json.content.page.image;

import com.enonic.wem.api.content.page.image.ImageDescriptors;


public class ImageDescriptorsJson
{
    private final ImageDescriptors descriptors;

    public ImageDescriptorsJson( final ImageDescriptors descriptors )
    {
        this.descriptors = descriptors;
    }

    public ImageDescriptors getDescriptors()
    {
        return descriptors;
    }
}
