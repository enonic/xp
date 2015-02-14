package com.enonic.xp.core.content.page.region;

import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.module.ModuleKeys;

public interface PartDescriptorService
{
    PartDescriptor getByKey( final DescriptorKey key );

    PartDescriptors getByModule( final ModuleKey moduleKey );

    PartDescriptors getByModules( final ModuleKeys moduleKeys );
}
