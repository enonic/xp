package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.Applications;
import com.enonic.xp.region.PartDescriptors;

final class GetPartDescriptorsByModulesCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorsByModulesCommand>
{
    private ApplicationKeys applicationKeys;

    public PartDescriptors execute()
    {
        final Applications applications = this.applicationService.getApplications( this.applicationKeys );
        return getDescriptorsFromModules( applications );
    }

    public GetPartDescriptorsByModulesCommand applicationKeys( final ApplicationKeys applicationKeys )
    {
        this.applicationKeys = applicationKeys;
        return this;
    }
}
