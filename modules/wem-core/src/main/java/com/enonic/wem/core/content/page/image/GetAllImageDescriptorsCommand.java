package com.enonic.wem.core.content.page.image;

import com.enonic.wem.api.content.page.image.ImageDescriptors;
import com.enonic.wem.api.module.Modules;

final class GetAllImageDescriptorsCommand
    extends AbstractGetImageDescriptorCommand<GetAllImageDescriptorsCommand>
{
    public ImageDescriptors execute()
    {
        final Modules modules = this.moduleService.getAllModules();
        return getImageDescriptorsFromModules( modules );
    }
}
