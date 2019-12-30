package com.enonic.xp.service;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public interface ServiceDescriptorService
{
    ServiceDescriptor getByKey( final DescriptorKey descriptorKey );

    ServiceDescriptors getByApplication( final ApplicationKey applicationKey );
}
