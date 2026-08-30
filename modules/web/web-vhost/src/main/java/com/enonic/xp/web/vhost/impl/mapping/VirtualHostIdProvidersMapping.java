package com.enonic.xp.web.vhost.impl.mapping;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;

public class VirtualHostIdProvidersMapping
{
    private final IdProviderKey defaultIdProvider;

    private final Map<IdProviderKey, Set<String>> idProviders;

    public VirtualHostIdProvidersMapping( final Builder builder )
    {
        this.defaultIdProvider = builder.defaultIdProvider;
        this.idProviders = Collections.unmodifiableMap( new LinkedHashMap<>( builder.idProviders ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IdProviderKey getDefaultIdProvider()
    {
        return defaultIdProvider;
    }

    public IdProviderKeys getIdProviderKeys()
    {
        return IdProviderKeys.from( idProviders.keySet() );
    }

    /**
     * The flow list configured for the given id provider: null when it is enabled without
     * restriction, an empty set when it is not enabled here.
     */
    public @Nullable Set<String> getFlows( final IdProviderKey idProviderKey )
    {
        return idProviders.containsKey( idProviderKey ) ? idProviders.get( idProviderKey ) : Set.of();
    }

    public static class Builder
    {
        private IdProviderKey defaultIdProvider;

        // Insertion-ordered so the (non-default) iteration order is stable. A null value means
        // the id provider is enabled without a flow restriction.
        private final Map<IdProviderKey, Set<String>> idProviders = new LinkedHashMap<>();

        private Builder()
        {
        }

        public Builder setDefaultIdProvider( final IdProviderKey defaultIdProvider )
        {
            this.defaultIdProvider = defaultIdProvider;
            this.idProviders.putIfAbsent( defaultIdProvider, null );
            return this;
        }

        public Builder addIdProviderKey( final IdProviderKey idProviderKey )
        {
            this.idProviders.putIfAbsent( idProviderKey, null );
            return this;
        }

        public Builder addIdProvider( final IdProviderKey idProviderKey, @Nullable final Set<String> flows )
        {
            this.idProviders.put( idProviderKey, flows == null || flows.isEmpty() ? null : Set.copyOf( flows ) );
            return this;
        }

        public VirtualHostIdProvidersMapping build()
        {
            return new VirtualHostIdProvidersMapping( this );
        }
    }
}
