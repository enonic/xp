package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.Applications;
import com.enonic.xp.region.LayoutDescriptors;

final class GetLayoutDescriptorsByApplicationsCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorsByApplicationsCommand>
{
    private ApplicationKeys applicationKeys;

    public LayoutDescriptors execute()
    {
        final Applications applications = this.applicationService.getApplications( this.applicationKeys );
        return getDescriptorsFromApplications( applications );
    }

    public GetLayoutDescriptorsByApplicationsCommand applicationKeys( final ApplicationKeys applicationKeys )
    {
        this.applicationKeys = applicationKeys;
        return this;
    }
}
