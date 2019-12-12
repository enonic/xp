package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.resource.ResourceService;

@org.osgi.service.component.annotations.Component
public final class ComponentServiceImpl
    implements ComponentService
{
    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private ResourceService resourceService;

    @Override
    public Component getByKey( final DescriptorKey descriptorKey )
    {
        return new GetComponentByNameCommand().
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            resourceService( this.resourceService ).
            descriptorKey( descriptorKey ).
            execute();
    }

    @Reference
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
