package com.enonic.wem.api.content.page.image;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;

public interface ImageDescriptorService
{
    ImageDescriptor getImageDescriptor( ImageDescriptorKey key )
        throws ImageDescriptorNotFoundException;

    ImageDescriptors getAllImageDescriptors();

    ImageDescriptors getImageDescriptorsByModule( ModuleKey moduleKey );

    ImageDescriptors getImageDescriptorsByModules( ModuleKeys moduleKeys );
}
