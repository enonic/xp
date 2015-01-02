package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageDescriptors;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;

public class PageDescriptorServiceImpl
    implements PageDescriptorService
{
    private ModuleService moduleService;

    public PageDescriptor getByKey( final DescriptorKey key )
    {
        return new GetPageDescriptorCommand().key( key ).execute();
    }

    @Override
    public PageDescriptors getByModule( final ModuleKey moduleKey )
    {
        return new GetPageDescriptorsByModuleCommand().moduleService( this.moduleService ).moduleKey( moduleKey ).execute();
    }

    @Override
    public PageDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        return new GetPageDescriptorsByModulesCommand().moduleService( this.moduleService ).moduleKeys( moduleKeys ).execute();
    }

    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
