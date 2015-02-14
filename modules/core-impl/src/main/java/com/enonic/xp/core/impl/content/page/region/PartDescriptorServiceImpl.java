package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.region.PartDescriptor;
import com.enonic.wem.api.content.page.region.PartDescriptorService;
import com.enonic.wem.api.content.page.region.PartDescriptors;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;

@Component
public final class PartDescriptorServiceImpl
    implements PartDescriptorService
{
    private ModuleService moduleService;

    public PartDescriptor getByKey( final DescriptorKey key )
    {
        return new GetPartDescriptorCommand().moduleService( this.moduleService ).key( key ).execute();
    }

    public PartDescriptors getByModule( final ModuleKey moduleKey )
    {
        return new GetPartDescriptorsByModuleCommand().moduleService( this.moduleService ).moduleKey( moduleKey ).execute();
    }

    public PartDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        return new GetPartDescriptorsByModulesCommand().moduleService( this.moduleService ).moduleKeys( moduleKeys ).execute();
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
