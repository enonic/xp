package com.enonic.wem.core.content.page.layout;

import javax.inject.Inject;

import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptors;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.resource.ResourceService;

public final class LayoutDescriptorServiceImpl
    implements LayoutDescriptorService
{
    @Inject
    protected ModuleService moduleService;

    @Inject
    protected ResourceService resourceService;

    public LayoutDescriptor getByKey( final LayoutDescriptorKey key )
    {
        return new GetLayoutDescriptorCommand().key( key ).moduleService( this.moduleService ).resourceService(
            this.resourceService ).execute();
    }

    public LayoutDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        return new GetLayoutDescriptorsByModulesCommand().moduleService( this.moduleService ).resourceService(
            this.resourceService ).moduleKeys( moduleKeys ).execute();
    }
}
