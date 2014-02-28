package com.enonic.wem.api.content.page.part;

import com.enonic.wem.api.module.ModuleKeys;

public interface PartDescriptorService
{
    PartDescriptor getByKey( final PartDescriptorKey key );

    PartDescriptor create( final CreatePartDescriptorParams params );

    PartDescriptors getByModules( final ModuleKeys moduleKeys );
}
