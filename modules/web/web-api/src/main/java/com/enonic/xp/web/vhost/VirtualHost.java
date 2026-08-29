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
     * The authentication flows enabled for the given id provider on this vhost: the XP-managed
     * flow names ({@link IdProviderFlow}) plus any informational names for the id provider app.
     * Implementations return an empty set for an id provider that is not enabled here; the default
     * implementation applies no restriction and returns {@link IdProviderFlow#DEFAULT} for any id
     * provider.
     */
    default Set<String> getIdProviderFlows( final IdProviderKey idProviderKey )
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
     * The principals allowed to pass through this vhost (the mapping's {@code allow} list), checked
     * before requests reach any handler: unauthenticated requests are rejected with 401,
     * authenticated ones without any of these principals with 403. An empty set (the default) means
     * no restriction.
     */
    default PrincipalKeys getAllowedPrincipals()
    {
        return PrincipalKeys.empty();
    }

    int getOrder();

    Map<String, String> getContext();
}
