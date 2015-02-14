package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.region.LayoutDescriptor;
import com.enonic.xp.content.page.region.LayoutDescriptorService;
import com.enonic.xp.content.page.region.LayoutDescriptors;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleKeys;
import com.enonic.xp.module.ModuleService;

@Component
public final class LayoutDescriptorServiceImpl
    implements LayoutDescriptorService
{
    private ModuleService moduleService;

    public LayoutDescriptor getByKey( final DescriptorKey key )
    {
        return new GetLayoutDescriptorCommand().key( key ).moduleService( this.moduleService ).execute();
    }

    public LayoutDescriptors getByModule( final ModuleKey moduleKey )
    {
        return new GetLayoutDescriptorsByModuleCommand().moduleService( this.moduleService ).moduleKey( moduleKey ).execute();
    }

    public LayoutDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        return new GetLayoutDescriptorsByModulesCommand().moduleService( this.moduleService ).moduleKeys( moduleKeys ).execute();
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
