package com.enonic.wem.portal.internal.content.page;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.Descriptor;
import com.enonic.wem.api.content.page.region.PartComponent;
import com.enonic.wem.api.content.page.region.PartDescriptorService;

public final class PartRenderer
    extends DescriptorBasedComponentRenderer<PartComponent>
{
    protected PartDescriptorService partDescriptorService;

    @Override
    public Class<PartComponent> getType()
    {
        return PartComponent.class;
    }

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return partDescriptorService.getByKey( descriptorKey );
    }

    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }
}
