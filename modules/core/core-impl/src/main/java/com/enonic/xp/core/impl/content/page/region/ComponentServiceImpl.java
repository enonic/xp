package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.ComponentService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;

@org.osgi.service.component.annotations.Component
public final class ComponentServiceImpl
    implements ComponentService
{
    protected PartDescriptorService partDescriptorService;

    protected LayoutDescriptorService layoutDescriptorService;

    @Override
    public Component getByName( final ApplicationKey applicationKey, final ComponentName name )
    {
        return new GetComponentByNameCommand().
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            applicationKey( applicationKey ).
            name( name ).
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
}
