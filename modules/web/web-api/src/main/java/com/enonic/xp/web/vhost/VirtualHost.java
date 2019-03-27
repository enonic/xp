package com.enonic.xp.web.vhost;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;

@Beta
public interface VirtualHost
{
    String getName();

    String getHost();

    String getSource();

    String getTarget();

    IdProviderKey getDefaultIdProviderKey();

    IdProviderKeys getIdProviderKeys();
}
