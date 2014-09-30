package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageDescriptors;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;

public class PageDescriptorServiceImpl
    implements PageDescriptorService
{
    @Inject
    protected ModuleService moduleService;

    public PageDescriptor getByKey( final PageDescriptorKey key )
    {
        return new GetPageDescriptorCommand().key( key ).execute();
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
