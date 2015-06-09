package com.enonic.xp.core.impl.content.page.region;

import com.enonic.xp.module.ModuleKeys;
import com.enonic.xp.module.Modules;
import com.enonic.xp.page.region.LayoutDescriptors;

final class GetLayoutDescriptorsByModulesCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorsByModulesCommand>
{
    private ModuleKeys moduleKeys;

    public LayoutDescriptors execute()
    {
        final Modules modules = this.moduleService.getModules( this.moduleKeys );
        return getDescriptorsFromModules( modules );
    }

    public GetLayoutDescriptorsByModulesCommand moduleKeys( final ModuleKeys moduleKeys )
    {
        this.moduleKeys = moduleKeys;
        return this;
    }
}
