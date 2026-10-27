package com.enonic.xp.web.vhost;

import java.util.Set;

import org.jspecify.annotations.NullMarked;

/**
 * An id provider's settings on one virtual host mapping ({@code mapping.<name>.idProvider.<idp>.*}):
 * the flows it serves there. Read through {@link VirtualHost#getIdProviders()}.
 */
@NullMarked
public final class VirtualHostIdProvider
{
    private final Set<String> flows;

    private final boolean allFlows;

    private VirtualHostIdProvider( final Builder builder )
    {
        this.flows = Set.copyOf( builder.flows );
        this.allFlows = builder.allFlows;
    }

    /**
     * A builder with no flows.
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
     * Whether the id provider serves a flow here. One of {@link IdProviderFlow#TOKEN_FLOWS} is served
     * when the flow list names it; any other flow is served unless the list leaves it out. The default
     * virtual host serves every flow.
     */
    public boolean serves( final String flow )
    {
        if ( allFlows )
        {
            return true;
        }
        return IdProviderFlow.TOKEN_FLOWS.contains( flow ) ? flows.contains( flow ) : flows.isEmpty() || flows.contains( flow );
    }

    /**
     * Builds the settings, normally from the vhost configuration. Each value replaces the previous.
     */
    public static final class Builder
    {
        private Set<String> flows = Set.of();

        private boolean allFlows;

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
         * Serves every flow, {@link IdProviderFlow#TOKEN_FLOWS} included, as the default virtual host
         * does.
         */
        public Builder allFlows()
        {
            this.allFlows = true;
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
