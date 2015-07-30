package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.Applications;
import com.enonic.xp.region.PartDescriptors;

final class GetPartDescriptorsByApplicationsCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorsByApplicationsCommand>
{
    private ApplicationKeys applicationKeys;

    public PartDescriptors execute()
    {
        final Applications applications = this.applicationService.getApplications( this.applicationKeys );
        return getDescriptorsFromApplications( applications );
    }

    public GetPartDescriptorsByApplicationsCommand applicationKeys( final ApplicationKeys applicationKeys )
    {
        this.applicationKeys = applicationKeys;
        return this;
    }
}
