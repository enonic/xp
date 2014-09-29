package com.enonic.wem.portal.internal.content.page;


import javax.inject.Inject;

import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorService;

public final class PartRenderer
    extends DescriptorBasedPageComponentRenderer<PartComponent>
{
    @Inject
    protected PartDescriptorService partDescriptorService;

    @Override
    public Class<PartComponent> getType()
    {
        return PartComponent.class;
    }

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return partDescriptorService.getByKey( (PartDescriptorKey) descriptorKey );
    }
}
