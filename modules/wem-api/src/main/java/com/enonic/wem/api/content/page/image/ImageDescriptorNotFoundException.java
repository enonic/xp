package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.NotFoundException;

public class ImageDescriptorNotFoundException
    extends NotFoundException
{
    public ImageDescriptorNotFoundException( final ImageDescriptorKey key, final Throwable cause )
    {
        super( cause, "ImageDescriptor [" + key.toString() + "] not found" );
    }
}
