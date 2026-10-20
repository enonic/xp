package com.enonic.xp.web.vhost;

import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.PrincipalKeys;

import static java.util.Objects.requireNonNull;

/**
 * An id provider's settings on one virtual host mapping ({@code mapping.<name>.idProvider.<idp>.*}):
 * the flows it serves there and the scope catalogue for device login. Read through
 * {@link VirtualHost#getIdProviders()}.
 */
@NullMarked
public final class VirtualHostIdProvider
{
    private final Set<String> flows;

    private final Map<String, Scope> scopes;

    private VirtualHostIdProvider( final Builder builder )
    {
        this.flows = Set.copyOf( builder.flows );
        this.scopes = Map.copyOf( builder.scopes );
    }

    /**
     * A builder with no flows and no scopes.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The flows configured for the id provider: XP's ({@link IdProviderFlow}) and any of the id
     * provider app's own. Empty means no restriction.
     */
    public Set<String> getFlows()
    {
        return flows;
    }

    /**
     * The scope catalogue ({@code mapping.<name>.idProvider.<idp>.scope.<id>.roles} and
     * {@code scope.<id>.required}): the scopes a client may ask for in a device login here. A token is
     * restricted to the roles of its scopes; a scope with no roles is a label. Empty means no
     * catalogue: no scope may be asked for.
     */
    public Map<String, Scope> getScopes()
    {
        return scopes;
    }

    /**
     * A scope in the catalogue.
     *
     * @param roles    the roles the scope implies; empty for a label-only scope
     * @param required whether the user may not drop it at consent
     */
    public record Scope(PrincipalKeys roles, boolean required)
    {
        public Scope
        {
            requireNonNull( roles, "roles is required" );
        }

        /**
         * A scope implying the given roles that the user may drop.
         */
        public static Scope of( final PrincipalKeys roles )
        {
            return new Scope( roles, false );
        }
    }

    /**
     * Builds the settings, normally from the vhost configuration. Each value replaces the previous.
     */
    public static final class Builder
    {
        private Set<String> flows = Set.of();

        private Map<String, Scope> scopes = Map.of();

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
        public Builder scopes( final Map<String, Scope> scopes )
        {
            this.scopes = scopes;
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
