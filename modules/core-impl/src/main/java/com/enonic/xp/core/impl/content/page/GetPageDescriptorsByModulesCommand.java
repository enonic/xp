package com.enonic.xp.core.impl.content.page;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.Resources;

final class GetPageDescriptorsByModulesCommand
    extends AbstractGetPageDescriptorCommand<GetPageDescriptorsByModulesCommand>
{
    private final static String PATH = "/site/pages";

    private ApplicationKeys applicationKeys;

    private ApplicationService applicationService;

    private ResourceService resourceService;


    public PageDescriptors execute()
    {
        final Applications applications = this.applicationService.getModules( this.applicationKeys );
        return getDescriptorsFromModules( applications );
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

    private PageDescriptors getDescriptorsFromModules( final Applications applications )
    {
        final List<PageDescriptor> pageDescriptors = new ArrayList<>();
        for ( final Application application : applications )
        {
            final Resources resources = this.resourceService.findResources( application.getKey(), PATH, "*", false );

            for ( final Resource resource : resources )
            {
                final String descriptorName = resource.getKey().getName();
                final DescriptorKey key = DescriptorKey.from( application.getKey(), descriptorName );
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