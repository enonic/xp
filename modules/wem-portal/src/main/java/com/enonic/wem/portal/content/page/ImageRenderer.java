package com.enonic.wem.portal.content.page;


import javax.inject.Inject;

import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.site.SiteTemplateKey;

import static com.enonic.wem.api.command.Commands.page;

public final class ImageRenderer
    extends PageComponentRenderer
{
    @Inject
    protected ImageDescriptorService imageDescriptorService;

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return imageDescriptorService.getImageDescriptor( (ImageDescriptorKey) descriptorKey );
    }
}
