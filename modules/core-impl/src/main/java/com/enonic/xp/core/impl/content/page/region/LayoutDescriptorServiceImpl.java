package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleKeys;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.region.LayoutDescriptor;
import com.enonic.xp.page.region.LayoutDescriptorService;
import com.enonic.xp.page.region.LayoutDescriptors;
import com.enonic.xp.schema.mixin.MixinService;

@Component
public final class LayoutDescriptorServiceImpl
    implements LayoutDescriptorService
{
    private ModuleService moduleService;

    private MixinService mixinService;

    @Override
    public LayoutDescriptor getByKey( final DescriptorKey key )
    {
        return new GetLayoutDescriptorCommand().key( key ).
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public LayoutDescriptors getByModule( final ModuleKey moduleKey )
    {
        return new GetLayoutDescriptorsByModuleCommand().
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
            moduleKey( moduleKey ).execute();
    }

    @Override
    public LayoutDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        return new GetLayoutDescriptorsByModulesCommand().
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
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
}
