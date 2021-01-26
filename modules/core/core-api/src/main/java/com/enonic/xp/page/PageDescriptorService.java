package com.enonic.xp.page;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;

@PublicApi
public interface PageDescriptorService
{
    PageDescriptor getByKey( DescriptorKey key );

    PageDescriptors getByApplication( ApplicationKey applicationKey );

    PageDescriptors getByApplications( ApplicationKeys applicationKeys );
}
