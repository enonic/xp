package com.enonic.xp.core.content.page;

import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.module.ModuleKeys;

public interface PageDescriptorService
{
    PageDescriptor getByKey( final DescriptorKey key );

    PageDescriptors getByModule( final ModuleKey moduleKey );

    PageDescriptors getByModules( final ModuleKeys moduleKeys );

}
