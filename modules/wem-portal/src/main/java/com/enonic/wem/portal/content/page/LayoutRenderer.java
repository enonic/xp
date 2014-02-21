package com.enonic.wem.portal.content.page;


import javax.inject.Inject;

import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;

public final class LayoutRenderer
    extends PageComponentRenderer
{
    @Inject
    protected LayoutDescriptorService layoutDescriptorService;

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return layoutDescriptorService.getByKey( (LayoutDescriptorKey) descriptorKey );
    }
}
