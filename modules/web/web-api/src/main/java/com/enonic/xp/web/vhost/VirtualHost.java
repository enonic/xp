package com.enonic.xp.web.vhost;

import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.dispatch.DispatchConstants;

/**
 * A virtual host mapping, as configured in {@code com.enonic.xp.web.vhost.cfg}.
 */
@NullMarked
public interface VirtualHost
{
    /**
     * Returns the mapping's name: the {@code <name>} of its {@code mapping.<name>.*} configuration keys.
     */
    String getName();

    /**
     * Returns the host requests must match: a host name, or a regular expression when prefixed with {@code ~}.
     */
    String getHost();

    /**
     * Returns the path prefix requests must match, in the client-facing URL space.
     */
    String getSource();

    /**
     * Returns the path prefix the source is rewritten to, in the internal URL space.
     */
    String getTarget();

    /**
     * Returns the vhost's default id provider, or null when none is configured.
     */
    @Nullable
    IdProviderKey getDefaultIdProviderKey();

    /**
     * Returns the id providers enabled on this vhost.
     */
    IdProviderKeys getIdProviderKeys();

    /**
     * Returns the flow list configured for the given id provider on this vhost: XP-managed flow
     * names ({@link IdProviderFlow}) plus any additional flows of the id provider app itself. An
     * empty set means no restriction: the id provider serves whatever flows it supports.
     */
    default Set<String> getIdProviderFlows( final IdProviderKey idProviderKey )
    {
        return Set.of();
    }

    /**
     * Returns the connector this vhost applies to: web ({@code xp}, the default), management
     * ({@code api}) or statistics ({@code status}).
     */
    default String getConnector()
    {
        return DispatchConstants.XP_CONNECTOR;
    }

    /**
     * Returns the principals allowed to pass through this vhost (the mapping's {@code allow} list).
     * An empty set, the default, means no restriction.
     */
    default PrincipalKeys getAllowedPrincipals()
    {
        return PrincipalKeys.empty();
    }

    /**
     * Returns the mapping's order: mappings with lower values are matched first.
     */
    int getOrder();

    /**
     * Returns the mapping's context attributes ({@code mapping.<name>.context.*}), copied into the
     * request's execution context.
     */
    Map<String, String> getContext();
}
