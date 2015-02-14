package com.enonic.xp.core.impl.content.page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.content.page.PageDescriptor;
import com.enonic.xp.core.content.page.PageDescriptorService;
import com.enonic.xp.core.content.page.PageDescriptors;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.module.ModuleKeys;
import com.enonic.xp.core.module.ModuleService;

@Component(immediate = true)
public final class PageDescriptorServiceImpl
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

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
