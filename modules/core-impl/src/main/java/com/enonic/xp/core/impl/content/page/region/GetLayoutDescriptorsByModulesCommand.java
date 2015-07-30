package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.Applications;
import com.enonic.xp.region.LayoutDescriptors;

final class GetLayoutDescriptorsByModulesCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorsByModulesCommand>
{
    private ApplicationKeys applicationKeys;

    public LayoutDescriptors execute()
    {
        final Applications applications = this.applicationService.getApplications( this.applicationKeys );
        return getDescriptorsFromModules( applications );
    }

    public GetLayoutDescriptorsByModulesCommand applicationKeys( final ApplicationKeys applicationKeys )
    {
        this.applicationKeys = applicationKeys;
        return this;
    }
}
