package com.enonic.xp.app;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ApplicationDescriptorService
{
    ApplicationDescriptor get( ApplicationKey key );
}
