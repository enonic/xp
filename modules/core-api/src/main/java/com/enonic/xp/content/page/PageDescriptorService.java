package com.enonic.xp.content.page;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleKeys;

public interface PageDescriptorService
{
    PageDescriptor getByKey( final DescriptorKey key );

    PageDescriptors getByModule( final ModuleKey moduleKey );

    PageDescriptors getByModules( final ModuleKeys moduleKeys );

}
