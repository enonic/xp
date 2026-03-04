package com.enonic.xp.service;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;


public interface ServiceDescriptorService
{
    ServiceDescriptor getByKey( DescriptorKey descriptorKey );

    ServiceDescriptors getByApplication( ApplicationKey applicationKey );
}
