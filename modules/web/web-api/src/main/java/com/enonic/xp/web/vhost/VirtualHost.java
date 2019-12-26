package com.enonic.xp.web.vhost;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;

@PublicApi
public interface VirtualHost
{
    String getName();

    String getHost();

    String getSource();

    String getTarget();

    IdProviderKey getDefaultIdProviderKey();

    IdProviderKeys getIdProviderKeys();
}
