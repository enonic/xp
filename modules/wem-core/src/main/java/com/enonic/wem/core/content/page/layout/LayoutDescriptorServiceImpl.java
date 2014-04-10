package com.enonic.wem.core.content.page.layout;

import javax.inject.Inject;

import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptors;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;

public final class LayoutDescriptorServiceImpl
    implements LayoutDescriptorService
{
    @Inject
    protected ModuleService moduleService;

    @Inject
    protected LayoutDescriptorService layoutDescriptorService;

    public LayoutDescriptor getByKey( final LayoutDescriptorKey key )
    {
        return new GetLayoutDescriptorCommand().key( key ).moduleService( this.moduleService ).execute();
    }

    public LayoutDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        return new GetLayoutDescriptorsByModulesCommand().moduleService( this.moduleService ).layoutDescriptorService(
            this.layoutDescriptorService ).moduleKeys( moduleKeys ).execute();
    }
}
