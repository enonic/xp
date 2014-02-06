package com.enonic.wem.portal.content.page;


import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;

import static com.enonic.wem.api.command.Commands.page;

public final class PartRenderer
    extends PageComponentRenderer
{

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return this.client.execute( page().descriptor().part().getByKey( (PartDescriptorKey) descriptorKey ) );
    }
}
