package com.enonic.xp.web.vhost;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

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

    /**
     * The authentication flows enabled for the given id provider on this vhost, or an empty set if
     * the id provider is not enabled here. Defaults to all flows (no restriction).
     */
    default Set<IdProviderFlow> getIdProviderFlows( final IdProviderKey idProviderKey )
    {
        return EnumSet.allOf( IdProviderFlow.class );
    }

    int getOrder();

    Map<String, String> getContext();
}
