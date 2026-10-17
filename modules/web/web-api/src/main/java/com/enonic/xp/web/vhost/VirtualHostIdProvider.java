package com.enonic.xp.web.vhost;

import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.PrincipalKeys;

/**
 * An id provider's settings on one virtual host mapping ({@code mapping.<name>.idProvider.<idp>.*}):
 * which flows it serves there, and the scope catalogue device login offers for it. The same id
 * provider may be configured differently on another vhost. A value: built by the vhost
 * configuration and read through {@link VirtualHost#getIdProviders()}.
 */
@NullMarked
public final class VirtualHostIdProvider
{
    private final Set<String> flows;

    private final Map<String, PrincipalKeys> scopes;

    private final Set<String> requiredScopes;

    private VirtualHostIdProvider( final Builder builder )
    {
        this.flows = Set.copyOf( builder.flows );
        this.scopes = Map.copyOf( builder.scopes );
        this.requiredScopes = Set.copyOf( builder.requiredScopes );
    }

    /**
     * A builder with no flows, no scopes and no required scopes.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Returns the flow list configured for the id provider: XP-managed flow names
     * ({@link IdProviderFlow}) plus any additional flows of the id provider app itself. An empty
     * set means no restriction: the id provider serves whatever flows it supports.
     */
    public Set<String> getFlows()
    {
        return flows;
    }

    /**
     * Returns the scope catalogue of the id provider on this vhost
     * ({@code mapping.<name>.idProvider.<idp>.scope.<id> = role, ...}): what a client may ask
     * for when a user of the id provider signs a device in here, and the roles each scope implies.
     * A token granted here is restricted to the roles of its scopes; a scope with no roles is a
     * label for applications to check. An empty map means no catalogue: no scope may be asked for
     * and tokens carry the user's full memberships.
     */
    public Map<String, PrincipalKeys> getScopes()
    {
        return scopes;
    }

    /**
     * Returns the scopes a user may not drop on the consent page
     * ({@code mapping.<name>.idProvider.<idp>.requiredScope = id, ...}): when a client asks for
     * one of them it is granted with the request or the request is denied as a whole. Empty means
     * every requested scope is the user's to keep or drop.
     */
    public Set<String> getRequiredScopes()
    {
        return requiredScopes;
    }

    /**
     * Assembles the settings, normally from the vhost configuration. Each value replaces the
     * previous one; the collections are copied on build.
     */
    public static final class Builder
    {
        private Set<String> flows = Set.of();

        private Map<String, PrincipalKeys> scopes = Map.of();

        private Set<String> requiredScopes = Set.of();

        private Builder()
        {
        }

        /**
         * The flow list; empty (the default) for no restriction. See {@link #getFlows()}.
         */
        public Builder flows( final Set<String> flows )
        {
            this.flows = flows;
            return this;
        }

        /**
         * The scope catalogue; empty (the default) for none. See {@link #getScopes()}.
         */
        public Builder scopes( final Map<String, PrincipalKeys> scopes )
        {
            this.scopes = scopes;
            return this;
        }

        /**
         * The scopes a user may not drop; empty (the default) for none. See {@link #getRequiredScopes()}.
         */
        public Builder requiredScopes( final Set<String> requiredScopes )
        {
            this.requiredScopes = requiredScopes;
            return this;
        }

        /**
         * Builds the settings.
         */
        public VirtualHostIdProvider build()
        {
            return new VirtualHostIdProvider( this );
        }
    }
}
