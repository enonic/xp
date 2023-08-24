package com.enonic.xp.portal.impl.api;

import com.enonic.xp.app.ApplicationKey;

public interface ApiDescriptorService
{
    ApiDescriptor getByApplication( ApplicationKey applicationKey );
}
