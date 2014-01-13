package com.enonic.wem.portal.rendering;


import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;

import static com.enonic.wem.api.command.Commands.page;

public final class ImageRenderer
    extends BaseComponentRenderer
{

    @Override
    protected Template getComponentTemplate( final TemplateKey componentTemplateKey )
    {
        try
        {
            return client.execute( page().template().image().getByKey().key( (ImageTemplateKey) componentTemplateKey ) );
        }
        catch ( NotFoundException e )
        {
            throw new RenderException( e, "Image template [{0}] not found.", componentTemplateKey.toString() );
        }
    }

    @Override
    protected Descriptor getComponentDescriptor( final Template template )
    {
        final DescriptorKey descriptorKey = template.getDescriptor();
        return this.client.execute( page().descriptor().image().getByKey( (ImageDescriptorKey) descriptorKey ) );
    }
}
