package com.enonic.wem.api.content.page.part;

import com.enonic.wem.api.command.content.page.part.CreatePartDescriptor;
import com.enonic.wem.api.module.ModuleKeys;

public interface PartDescriptorService
{
    PartDescriptor getByKey( final PartDescriptorKey key );

    PartDescriptor create( final CreatePartDescriptor command );

    PartDescriptors getByModules( final ModuleKeys moduleKeys );
}
