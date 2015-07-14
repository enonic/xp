package com.enonic.xp.core.impl.content.page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleKeys;
import com.enonic.xp.module.ModuleService;
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
    private ModuleService moduleService;

    private MixinService mixinService;

    private ResourceService resourceService;

    @Override
    public PageDescriptor getByKey( final DescriptorKey key )
    {
        return new GetPageDescriptorCommand().
            mixinService( this.mixinService ).
            key( key ).execute();
    }

    @Override
    public PageDescriptors getByModule( final ModuleKey moduleKey )
    {
        return new GetPageDescriptorsByModuleCommand().
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
            resourceService( this.resourceService ).
            moduleKey( moduleKey ).execute();
    }

    @Override
    public PageDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        return new GetPageDescriptorsByModulesCommand().
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
            resourceService( this.resourceService ).
            moduleKeys( moduleKeys ).execute();
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
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
