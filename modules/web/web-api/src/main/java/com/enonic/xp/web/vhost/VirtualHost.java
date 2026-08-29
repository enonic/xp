package com.enonic.xp.web.vhost;

import java.util.Map;
import java.util.Set;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.security.PrincipalKeys;
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
     * The connector this vhost mapping applies to. Defaults to the web ({@code xp}) connector, so
     * mappings never apply to the management ({@code api}) or statistics ({@code status}) endpoints
     * unless explicitly configured to.
     */
    default String getConnector()
    {
        return DispatchConstants.XP_CONNECTOR;
    }

    /**
     * The principals allowed to pass through this vhost, checked before requests reach any handler:
     * unauthenticated requests are rejected with 401, authenticated ones without any of these
     * principals with 403. An empty set (the default) means no restriction.
     */
    default PrincipalKeys getAllowedPrincipals()
    {
        return PrincipalKeys.empty();
    }

    int getOrder();

    Map<String, String> getContext();
}
