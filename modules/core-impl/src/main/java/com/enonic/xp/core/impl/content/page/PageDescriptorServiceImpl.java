package com.enonic.xp.core.impl.content.page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;

@Component(immediate = true)
public final class PageDescriptorServiceImpl
    implements PageDescriptorService
{
    private ApplicationService applicationService;

    private MixinService mixinService;

    private ResourceService resourceService;

    @Override
    public PageDescriptor getByKey( final DescriptorKey key )
    {
        return new GetPageDescriptorCommand().
            mixinService( this.mixinService ).
            key( key ).
            resourceService( this.resourceService ).
            execute();
    }

    @Override
    public PageDescriptors getByApplication( final ApplicationKey applicationKey )
    {
        return new GetPageDescriptorsByModuleCommand().
            applicationService( this.applicationService ).
            mixinService( this.mixinService ).
            resourceService( this.resourceService ).
            applicationKey( applicationKey ).execute();
    }

    @Override
    public PageDescriptors getByApplications( final ApplicationKeys applicationKeys )
    {
        return new GetPageDescriptorsByModulesCommand().
            applicationService( this.applicationService ).
            mixinService( this.mixinService ).
            resourceService( this.resourceService ).
            applicationKeys( applicationKeys ).execute();
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
