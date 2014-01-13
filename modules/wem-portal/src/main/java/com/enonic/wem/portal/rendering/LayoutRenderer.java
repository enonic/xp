package com.enonic.wem.portal.rendering;


import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;

import static com.enonic.wem.api.command.Commands.page;

public final class LayoutRenderer
    extends BaseComponentRenderer
{

    @Override
    protected Template getComponentTemplate( final TemplateKey componentTemplateKey )
    {
        try
        {
            return client.execute( page().template().layout().getByKey().key( (LayoutTemplateKey) componentTemplateKey ) );
        }
        catch ( NotFoundException e )
        {
            throw new RenderException( e, "Layout template [{0}] not found.", componentTemplateKey.toString() );
        }
    }

    @Override
    protected Descriptor getComponentDescriptor( final Template template )
    {
        final DescriptorKey descriptorKey = template.getDescriptor();
        return this.client.execute( page().descriptor().layout().getByKey( (LayoutDescriptorKey) descriptorKey ) );
    }
}
