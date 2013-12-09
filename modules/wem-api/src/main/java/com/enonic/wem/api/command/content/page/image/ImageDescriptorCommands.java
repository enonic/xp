package com.enonic.wem.api.command.content.page.image;


import com.enonic.wem.api.content.page.image.ImageDescriptor;

public final class ImageDescriptorCommands
{
    public CreateImageDescriptor create()
    {
        return new CreateImageDescriptor();
    }

    public CreateImageDescriptor create( final ImageDescriptor imageDescriptor )
    {
        return new CreateImageDescriptor( imageDescriptor );
    }
}
