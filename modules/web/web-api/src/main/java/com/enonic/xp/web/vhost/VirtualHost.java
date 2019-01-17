package com.enonic.xp.web.vhost;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.IdProviderKey;

@Beta
public interface VirtualHost
{
    String getName();

    String getHost();

    String getSource();

    String getTarget();

    IdProviderKey getUserStoreKey();
}
