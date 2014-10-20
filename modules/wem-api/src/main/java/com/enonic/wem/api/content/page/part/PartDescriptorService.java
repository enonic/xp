package com.enonic.wem.api.content.page.part;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;

public interface PartDescriptorService
{
    PartDescriptor getByKey( final PartDescriptorKey key );

    PartDescriptors getByModule( final ModuleKey moduleKey );

    PartDescriptors getByModules( final ModuleKeys moduleKeys );
}
