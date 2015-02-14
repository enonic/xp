package com.enonic.xp.core.content.page.region;

import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.module.ModuleKeys;

public interface LayoutDescriptorService
{
    LayoutDescriptor getByKey( final DescriptorKey key );

    LayoutDescriptors getByModule( final ModuleKey moduleKey );

    LayoutDescriptors getByModules( final ModuleKeys moduleKeys );
}
