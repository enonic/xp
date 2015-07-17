package com.enonic.xp.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.page.DescriptorKey;

@Beta
public interface PartDescriptorService
{
    PartDescriptor getByKey( final DescriptorKey key );

    PartDescriptors getByModule( final ApplicationKey applicationKey );

    PartDescriptors getByModules( final ApplicationKeys applicationKeys );
}
