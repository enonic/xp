package com.enonic.xp.content.page.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleKeys;

@Beta
public interface PartDescriptorService
{
    PartDescriptor getByKey( final DescriptorKey key );

    PartDescriptors getByModule( final ModuleKey moduleKey );

    PartDescriptors getByModules( final ModuleKeys moduleKeys );
}
