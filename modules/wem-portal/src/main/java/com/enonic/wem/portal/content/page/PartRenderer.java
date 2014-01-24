package com.enonic.wem.portal.content.page;


import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.portal.rendering.RenderException;

import static com.enonic.wem.api.command.Commands.page;

public final class PartRenderer
    extends PageComponentRenderer
{

    @Override
    protected Template getComponentTemplate( final TemplateKey componentTemplateKey )
    {
        try
        {
            return client.execute( page().template().part().getByKey().key( (PartTemplateKey) componentTemplateKey ) );
        }
        catch ( NotFoundException e )
        {
            throw new RenderException( e, "Part template [{0}] not found.", componentTemplateKey.toString() );
        }
    }

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return this.client.execute( page().descriptor().part().getByKey( (PartDescriptorKey) descriptorKey ) );
    }
}
