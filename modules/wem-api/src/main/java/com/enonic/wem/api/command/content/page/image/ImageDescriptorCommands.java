package com.enonic.wem.api.command.content.page.image;


import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;

public final class ImageDescriptorCommands
{
    public GetImageDescriptor getByKey( final ImageDescriptorKey key )
    {
        return new GetImageDescriptor( key );
    }

    public CreateImageDescriptor create()
    {
        return new CreateImageDescriptor();
    }

    public CreateImageDescriptor create( final ImageDescriptor imageDescriptor )
    {
        return new CreateImageDescriptor( imageDescriptor );
    }
}
