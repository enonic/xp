package com.enonic.xp.api;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;


public interface ApiDescriptorService
{
    ApiDescriptor getByKey( DescriptorKey descriptorKey );

    ApiDescriptors getByApplication( ApplicationKey applicationKey );

    ResourceKey getControllerResourceKey( DescriptorKey key );
}
