package com.enonic.xp.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.page.DescriptorKey;

@Beta
public interface LayoutDescriptorService
{
    LayoutDescriptor getByKey( final DescriptorKey key );

    LayoutDescriptors getByApplication( final ApplicationKey applicationKey );

    LayoutDescriptors getByApplications( final ApplicationKeys applicationKeys );
}
