package com.enonic.xp.core.impl.content.page;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.Resources;

final class GetPageDescriptorsByModuleCommand
    extends AbstractGetPageDescriptorCommand<GetPageDescriptorsByModuleCommand>
{
    private final static String PATH = "/app/pages";

    private ApplicationKey applicationKey;

    private ModuleService moduleService;

    private ResourceService resourceService;

    public PageDescriptors execute()
    {
        final Module module = this.moduleService.getModule( this.applicationKey );
        return getDescriptorsFromModule( module );
    }

    public GetPageDescriptorsByModuleCommand applicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }

    public final GetPageDescriptorsByModuleCommand moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return this;
    }

    public final GetPageDescriptorsByModuleCommand resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return this;
    }

    private PageDescriptors getDescriptorsFromModule( final Module module )
    {
        final List<PageDescriptor> pageDescriptors = new ArrayList<>();
        final Resources resources = this.resourceService.findResources( module.getKey(), PATH, "*", false );

        for ( final Resource resource : resources )
        {
            final String descriptorName = resource.getKey().getName();
            final DescriptorKey key = DescriptorKey.from( module.getKey(), descriptorName );
            final PageDescriptor pageDescriptor = getDescriptor( key );
            if ( pageDescriptor != null )
            {
                pageDescriptors.add( pageDescriptor );
            }

        }

        return PageDescriptors.from( pageDescriptors );
    }

}