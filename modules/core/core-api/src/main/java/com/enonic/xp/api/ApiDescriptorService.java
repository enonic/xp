package com.enonic.xp.api;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public interface ApiDescriptorService
{
    ApiDescriptor getByKey( DescriptorKey descriptorKey );

    ApiDescriptors getByApplication( ApplicationKey applicationKey );
}
