package com.enonic.xp.app;

import com.google.common.annotations.Beta;

@Beta
public interface ApplicationDescriptorService
{
    ApplicationDescriptor get( ApplicationKey key );
}
