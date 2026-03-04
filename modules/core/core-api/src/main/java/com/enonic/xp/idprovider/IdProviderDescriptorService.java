package com.enonic.xp.idprovider;

import com.enonic.xp.app.ApplicationKey;


public interface IdProviderDescriptorService
{
    IdProviderDescriptor getDescriptor( ApplicationKey key );
}
