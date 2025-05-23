package com.enonic.xp.page;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorKey;

@PublicApi
public interface PageDescriptorService
{
    PageDescriptor getByKey( DescriptorKey key );

    PageDescriptors getByApplication( ApplicationKey applicationKey );

    PageDescriptors getByApplications( ApplicationKeys applicationKeys );
}
