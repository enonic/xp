package com.enonic.wem.api.command.content.page.image;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;

public class GetImageDescriptor
    extends Command<ImageDescriptor>
{
    private final ImageDescriptorKey key;

    public GetImageDescriptor( final ImageDescriptorKey key )
    {
        this.key = key;
    }

    public ImageDescriptorKey getKey()
    {
        return key;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }
}
