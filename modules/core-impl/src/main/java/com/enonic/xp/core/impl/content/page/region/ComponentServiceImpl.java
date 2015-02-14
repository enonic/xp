package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.core.content.page.region.Component;
import com.enonic.xp.core.content.page.region.ComponentName;
import com.enonic.xp.core.content.page.region.ComponentService;
import com.enonic.xp.core.content.page.region.LayoutDescriptorService;
import com.enonic.xp.core.content.page.region.PartDescriptorService;
import com.enonic.xp.core.module.ModuleKey;

@org.osgi.service.component.annotations.Component
public final class ComponentServiceImpl
    implements ComponentService
{
    protected PartDescriptorService partDescriptorService;

    protected LayoutDescriptorService layoutDescriptorService;

    @Override
    public Component getByName( final ModuleKey module, final ComponentName name )
    {
        return new GetComponentByNameCommand().
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            module( module ).
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
