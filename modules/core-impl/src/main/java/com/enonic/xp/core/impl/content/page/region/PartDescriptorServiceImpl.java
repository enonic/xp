package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleKeys;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.schema.mixin.MixinService;

@Component
public final class PartDescriptorServiceImpl
    implements PartDescriptorService
{
    private ModuleService moduleService;

    private MixinService mixinService;

    @Override
    public PartDescriptor getByKey( final DescriptorKey key )
    {
        return new GetPartDescriptorCommand().
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
            key( key ).execute();
    }

    @Override
    public PartDescriptors getByModule( final ModuleKey moduleKey )
    {
        return new GetPartDescriptorsByModuleCommand().
            moduleService( this.moduleService ).
            mixinService( this.mixinService ).
            moduleKey( moduleKey ).execute();
    }

    @Override
    public PartDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        return new GetPartDescriptorsByModulesCommand().
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
