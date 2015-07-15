package com.enonic.xp.page;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.module.ModuleKeys;

@Beta
public interface PageDescriptorService
{
    PageDescriptor getByKey( final DescriptorKey key );

    PageDescriptors getByModule( final ApplicationKey applicationKey );

    PageDescriptors getByModules( final ModuleKeys moduleKeys );

}
