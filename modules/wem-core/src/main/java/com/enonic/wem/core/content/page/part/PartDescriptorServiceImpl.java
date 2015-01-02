package com.enonic.wem.core.content.page.part;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptors;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;

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

    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
