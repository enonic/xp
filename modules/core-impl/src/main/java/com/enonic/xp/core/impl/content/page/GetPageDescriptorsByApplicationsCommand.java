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
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;

final class GetPageDescriptorsByApplicationsCommand
    extends AbstractGetPageDescriptorCommand<GetPageDescriptorsByApplicationsCommand>
{
    private final static String PATH = "/site/pages";

    private ApplicationKeys applicationKeys;

    private ApplicationService applicationService;

    public PageDescriptors execute()
    {
        final Applications applications = this.applicationService.getApplications( this.applicationKeys );
        return getDescriptorsFromApplications( applications );
    }

    public GetPageDescriptorsByApplicationsCommand applicationKeys( final ApplicationKeys applicationKeys )
    {
        this.applicationKeys = applicationKeys;
        return this;
    }

    public final GetPageDescriptorsByApplicationsCommand applicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
        return this;
    }

    private PageDescriptors getDescriptorsFromApplications( final Applications applications )
    {
        final List<PageDescriptor> pageDescriptors = new ArrayList<>();
        for ( final Application application : applications )
        {
            final ResourceKeys resourceKeys = this.resourceService.findResourceKeys( application.getKey(), PATH, "*", false );

            for ( final ResourceKey resourceKey : resourceKeys )
            {
                final String descriptorName = resourceKey.getName();
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