package com.enonic.xp.web.vhost;

import java.util.Map;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;


public interface VirtualHost
{
    String getName();

    String getHost();

    String getSource();

    String getTarget();

    IdProviderKey getDefaultIdProviderKey();

    IdProviderKeys getIdProviderKeys();

    int getOrder();

    Map<String, String> getContext();
}
