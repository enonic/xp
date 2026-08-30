package com.enonic.xp.web.vhost;

import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NullMarked;

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
     * Returns the authentication flows enabled for the given id provider on this vhost: XP-managed
     * flow names ({@link IdProviderFlow}) plus any informational names for the id provider app.
     * Returns an empty set for an id provider not enabled on this vhost, and
     * {@link IdProviderFlow#DEFAULT} where no flow restriction applies.
     */
    @NullMarked
    default Set<String> getIdProviderFlows( final IdProviderKey idProviderKey )
    {
        return IdProviderFlow.DEFAULT;
    }

    /**
     * Returns the connector this vhost applies to: web ({@code xp}, the default), management
     * ({@code api}) or statistics ({@code status}).
     */
    @NullMarked
    default String getConnector()
    {
        return DispatchConstants.XP_CONNECTOR;
    }

    /**
     * Returns the principals allowed to pass through this vhost (the mapping's {@code allow} list).
     * An empty set, the default, means no restriction.
     */
    @NullMarked
    default PrincipalKeys getAllowedPrincipals()
    {
        return PrincipalKeys.empty();
    }

    int getOrder();

    Map<String, String> getContext();
}
