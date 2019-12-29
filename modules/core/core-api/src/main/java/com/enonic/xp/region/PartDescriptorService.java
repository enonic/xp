package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public interface PartDescriptorService
{
    PartDescriptor getByKey( final DescriptorKey key );

    PartDescriptors getByApplication( final ApplicationKey applicationKey );

    PartDescriptors getByApplications( final ApplicationKeys applicationKeys );
}
