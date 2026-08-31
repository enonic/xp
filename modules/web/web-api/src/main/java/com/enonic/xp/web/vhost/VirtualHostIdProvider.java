package com.enonic.xp.web.vhost;

import java.util.Set;

import org.jspecify.annotations.NullMarked;

/**
 * An id provider's configuration on a virtual host mapping.
 */
@NullMarked
public final class VirtualHostIdProvider
{
    private final Set<String> flows;

    private VirtualHostIdProvider( final Builder builder )
    {
        this.flows = Set.copyOf( builder.flows );
    }

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

    public static final class Builder
    {
        private Set<String> flows = Set.of();

        private Builder()
        {
        }

        public Builder flows( final Set<String> flows )
        {
            this.flows = flows;
            return this;
        }

        public VirtualHostIdProvider build()
        {
            return new VirtualHostIdProvider( this );
        }
    }
}
