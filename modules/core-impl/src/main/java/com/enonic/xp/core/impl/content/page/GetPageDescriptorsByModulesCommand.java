package com.enonic.xp.core.impl.content.page;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.Modules;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.Resources;

final class GetPageDescriptorsByModulesCommand
    extends AbstractGetPageDescriptorCommand<GetPageDescriptorsByModulesCommand>
{
    private final static String PATH = "/app/pages";

    private ApplicationKeys applicationKeys;

    private ApplicationService applicationService;

    private ResourceService resourceService;


    public PageDescriptors execute()
    {
        final Modules modules = this.applicationService.getModules( this.applicationKeys );
        return getDescriptorsFromModules( modules );
    }

    public GetPageDescriptorsByModulesCommand applicationKeys( final ApplicationKeys applicationKeys )
    {
        this.applicationKeys = applicationKeys;
        return this;
    }

    public final GetPageDescriptorsByModulesCommand applicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
        return this;
    }

    public final GetPageDescriptorsByModulesCommand resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return this;
    }

    private PageDescriptors getDescriptorsFromModules( final Modules modules )
    {
        final List<PageDescriptor> pageDescriptors = new ArrayList<>();
        for ( final Module module : modules )
        {
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
        }

        return PageDescriptors.from( pageDescriptors );
    }

}