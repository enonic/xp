package com.enonic.xp.content.page.region;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleKeys;

public interface LayoutDescriptorService
{
    LayoutDescriptor getByKey( final DescriptorKey key );

    LayoutDescriptors getByModule( final ModuleKey moduleKey );

    LayoutDescriptors getByModules( final ModuleKeys moduleKeys );
}
