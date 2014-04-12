package com.enonic.wem.core.content.page.image;

import com.enonic.wem.api.content.page.image.ImageDescriptors;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.Modules;

final class GetImageDescriptorsByModulesCommand
    extends AbstractGetImageDescriptorCommand<GetImageDescriptorsByModulesCommand>
{
    private ModuleKeys moduleKeys;

    public ImageDescriptors execute()
    {
        final Modules modules = this.moduleService.getModules( this.moduleKeys );
        return getImageDescriptorsFromModules( modules );
    }

    public GetImageDescriptorsByModulesCommand modules( final ModuleKeys moduleKeys )
    {
        this.moduleKeys = moduleKeys;
        return this;
    }
}
