package com.enonic.wem.core.content.page.layout;

import com.enonic.wem.api.content.page.layout.LayoutDescriptors;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.Modules;

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
