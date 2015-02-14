package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.content.page.region.PartDescriptors;
import com.enonic.xp.module.ModuleKeys;
import com.enonic.xp.module.Modules;

final class GetPartDescriptorsByModulesCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorsByModulesCommand>
{
    private ModuleKeys moduleKeys;

    public PartDescriptors execute()
    {
        final Modules modules = this.moduleService.getModules( this.moduleKeys );
        return getDescriptorsFromModules( modules );
    }

    public GetPartDescriptorsByModulesCommand moduleKeys( final ModuleKeys moduleKeys )
    {
        this.moduleKeys = moduleKeys;
        return this;
    }
}
