package com.enonic.xp.page;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;

@PublicApi
public interface PageDescriptorService
{
    PageDescriptor getByKey( final DescriptorKey key );

    PageDescriptors getByApplication( final ApplicationKey applicationKey );

    PageDescriptors getByApplications( final ApplicationKeys applicationKeys );
}
