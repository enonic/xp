package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.module.Modules;
import com.enonic.xp.region.PartDescriptors;

final class GetPartDescriptorsByModulesCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorsByModulesCommand>
{
    private ApplicationKeys applicationKeys;

    public PartDescriptors execute()
    {
        final Modules modules = this.applicationService.getModules( this.applicationKeys );
        return getDescriptorsFromModules( modules );
    }

    public GetPartDescriptorsByModulesCommand applicationKeys( final ApplicationKeys applicationKeys )
    {
        this.applicationKeys = applicationKeys;
        return this;
    }
}
