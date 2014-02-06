package com.enonic.wem.portal.content.page;


import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;

import static com.enonic.wem.api.command.Commands.page;

public final class LayoutRenderer
    extends PageComponentRenderer
{

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return this.client.execute( page().descriptor().layout().getByKey( (LayoutDescriptorKey) descriptorKey ) );
    }
}
