package com.enonic.xp.service;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;

@Beta
public interface ServiceDescriptorService
{
    ServiceDescriptor getByKey( final DescriptorKey descriptorKey );

    ServiceDescriptors getByApplication( final ApplicationKey applicationKey );
}
