package com.enonic.xp.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.module.ModuleKeys;
import com.enonic.xp.page.DescriptorKey;

@Beta
public interface LayoutDescriptorService
{
    LayoutDescriptor getByKey( final DescriptorKey key );

    LayoutDescriptors getByModule( final ApplicationKey applicationKey );

    LayoutDescriptors getByModules( final ModuleKeys moduleKeys );
}
