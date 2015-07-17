package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.module.Modules;
import com.enonic.xp.region.LayoutDescriptors;

final class GetLayoutDescriptorsByModulesCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorsByModulesCommand>
{
    private ApplicationKeys applicationKeys;

    public LayoutDescriptors execute()
    {
        final Modules modules = this.moduleService.getModules( this.applicationKeys );
        return getDescriptorsFromModules( modules );
    }

    public GetLayoutDescriptorsByModulesCommand applicationKeys( final ApplicationKeys applicationKeys )
    {
        this.applicationKeys = applicationKeys;
        return this;
    }
}
