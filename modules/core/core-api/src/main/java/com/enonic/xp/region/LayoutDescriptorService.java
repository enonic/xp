package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorKey;

@PublicApi
public interface LayoutDescriptorService
{
    LayoutDescriptor getByKey( DescriptorKey key );

    LayoutDescriptors getByApplication( ApplicationKey applicationKey );

    LayoutDescriptors getByApplications( ApplicationKeys applicationKeys );
}
