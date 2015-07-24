package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;

@Component
public final class PartDescriptorServiceImpl
    implements PartDescriptorService
{
    private ApplicationService applicationService;

    private MixinService mixinService;

    private ResourceService resourceService;

    @Override
    public PartDescriptor getByKey( final DescriptorKey key )
    {
        return new GetPartDescriptorCommand().
            applicationService( this.applicationService ).
            mixinService( this.mixinService ).
            resourceService( this.resourceService ).
            key( key ).
            execute();
    }

    @Override
    public PartDescriptors getByModule( final ApplicationKey applicationKey )
    {
        return new GetPartDescriptorsByModuleCommand().
            applicationService( this.applicationService ).
            mixinService( this.mixinService ).
            resourceService( this.resourceService ).
            applicationKey( applicationKey ).
            execute();
    }

    @Override
    public PartDescriptors getByModules( final ApplicationKeys applicationKeys )
    {
        return new GetPartDescriptorsByModulesCommand().
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
