package com.enonic.xp.web.vhost;

import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.security.PrincipalKeys;

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
     * Returns the vhost's default id provider - the first key of {@link #getIdProviders()} - or
     * null when none is enabled.
     */
    @Nullable
    IdProviderKey getDefaultIdProviderKey();

    /**
     * Returns the id providers enabled on this vhost.
     *
     * @deprecated Use the keys of {@link #getIdProviders()}.
     */
    @Deprecated
    IdProviderKeys getIdProviderKeys();

    /**
     * Returns the id providers enabled on this vhost with their per-vhost configuration
     * ({@link VirtualHostIdProvider}), the default id provider first.
     */
    Map<IdProviderKey, VirtualHostIdProvider> getIdProviders();

    /**
     * Returns the connector this vhost applies to: web ({@code xp}, the default), management
     * ({@code api}) or statistics ({@code status}).
     */
    String getConnector();

    /**
     * Returns the principals allowed to pass through this vhost (the mapping's {@code allow} list).
     * An empty set means no restriction.
     */
    PrincipalKeys getAllowedPrincipals();

    /**
     * Returns the mapping's order: mappings with lower values are matched first.
     */
    int getOrder();

    /**
     * The audiences this vhost is configured to serve (the mapping's {@code audience} list); empty
     * if none is declared, in which case the mapping serves its own {@link #identifier identifier}
     * and XP's issuer name. See {@link #acceptsAudience(Set, String, String)}.
     */
    Set<String> getAudience();

    /**
     * The mapping's own identifier as a resource (RFC 8707): the URL its source path is served at
     * for the given scheme and port, {@code https://host/source}, without a trailing slash. The
     * port is left out when it is the scheme's default or unknown. For a mapping matching hosts by
     * pattern it is the identifier for the given host name.
     *
     * @param scheme the request scheme, {@code http} or {@code https}
     * @param host   the host name the request was for
     * @param port   the request port, or a value no greater than zero when unknown
     */
    String identifier( String scheme, String host, int port );

    /**
     * Whether the mapping matches a host name: the mapping's host, one of its space-separated
     * hosts, or its regular expression when prefixed with {@code ~}.
     */
    boolean matchesHost( String serverName );

    /**
     * Whether a token with the given audiences is accepted here. The token must name one of the
     * declared audiences, or XP's issuer name {@code self} where none is declared, or a resource
     * under this mapping that the request is at or below: the mapping's own
     * {@link #identifier identifier}, or that identifier with more of the request's path.
     *
     * @param self       XP's issuer name
     * @param requestUrl the external URL the request came to, without query, or null when unknown
     */
    boolean acceptsAudience( Set<String> audience, String self, @Nullable String requestUrl );

    /**
     * Returns the mapping's context attributes ({@code mapping.<name>.context.*}), copied into the
     * request's execution context.
     */
    Map<String, String> getContext();
}
