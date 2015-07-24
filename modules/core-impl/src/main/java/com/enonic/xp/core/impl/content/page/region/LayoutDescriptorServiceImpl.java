package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;

@Component
public final class LayoutDescriptorServiceImpl
    implements LayoutDescriptorService
{
    private ApplicationService applicationService;

    private MixinService mixinService;

    private ResourceService resourceService;

    @Override
    public LayoutDescriptor getByKey( final DescriptorKey key )
    {
        return new GetLayoutDescriptorCommand().key( key ).
            applicationService( this.applicationService ).
            mixinService( this.mixinService ).
            resourceService( resourceService ).
            execute();
    }

    @Override
    public LayoutDescriptors getByModule( final ApplicationKey applicationKey )
    {
        return new GetLayoutDescriptorsByModuleCommand().
            applicationService( this.applicationService ).
            mixinService( this.mixinService ).
            resourceService( this.resourceService ).
            applicationKey( applicationKey ).
            execute();
    }

    @Override
    public LayoutDescriptors getByModules( final ApplicationKeys applicationKeys )
    {
        return new GetLayoutDescriptorsByModulesCommand().
            applicationService( this.applicationService ).
            mixinService( this.mixinService ).
            resourceService( this.resourceService ).
            applicationKeys( applicationKeys ).
            execute();
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
