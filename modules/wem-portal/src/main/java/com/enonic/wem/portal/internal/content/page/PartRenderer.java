package com.enonic.wem.portal.internal.content.page;


import javax.inject.Inject;

import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorService;

public final class PartRenderer
    extends DescriptorBasedPageComponentRenderer
{
    @Inject
    protected PartDescriptorService partDescriptorService;

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return partDescriptorService.getByKey( (PartDescriptorKey) descriptorKey );
    }
}
