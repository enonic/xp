package com.enonic.xp.web.vhost;

import java.util.Map;
import java.util.Set;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.web.dispatch.DispatchConstants;


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
     * the id provider is not enabled here. Defaults to {@link IdProviderFlow#DEFAULT}.
     */
    default Set<IdProviderFlow> getIdProviderFlows( final IdProviderKey idProviderKey )
    {
        return IdProviderFlow.DEFAULT;
    }

    /**
     * The connectors this vhost mapping applies to. Defaults to the {@code xp} connector only, so
     * mappings never apply to the management ({@code api}) or statistics ({@code status}) ports
     * unless explicitly configured to.
     */
    default Set<String> getConnectors()
    {
        return Set.of( DispatchConstants.XP_CONNECTOR );
    }

    int getOrder();

    Map<String, String> getContext();
}
