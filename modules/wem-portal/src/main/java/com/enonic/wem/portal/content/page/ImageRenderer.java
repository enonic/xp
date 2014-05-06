package com.enonic.wem.portal.content.page;


import javax.inject.Inject;

import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;

public final class ImageRenderer
    extends DescriptorBasedPageComponentRenderer
{
    @Inject
    protected ImageDescriptorService imageDescriptorService;

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return imageDescriptorService.getImageDescriptor( (ImageDescriptorKey) descriptorKey );
    }
}
