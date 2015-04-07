package com.enonic.xp.content.page.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleKeys;

@Beta
public interface LayoutDescriptorService
{
    LayoutDescriptor getByKey( final DescriptorKey key );

    LayoutDescriptors getByModule( final ModuleKey moduleKey );

    LayoutDescriptors getByModules( final ModuleKeys moduleKeys );
}
