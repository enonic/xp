package com.enonic.xp.idprovider;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public interface IdProviderDescriptorService
{
    IdProviderDescriptor getDescriptor( final ApplicationKey key );
}