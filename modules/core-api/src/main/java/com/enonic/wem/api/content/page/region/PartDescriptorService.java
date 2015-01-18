package com.enonic.wem.api.content.page.region;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;

public interface PartDescriptorService
{
    PartDescriptor getByKey( final DescriptorKey key );

    PartDescriptors getByModule( final ModuleKey moduleKey );

    PartDescriptors getByModules( final ModuleKeys moduleKeys );
}
