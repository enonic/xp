package com.enonic.xp.web.vhost.impl.mapping;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;

public class VirtualHostIdProvidersMapping
{
    private final Map<IdProviderKey, Set<String>> idProviders;

    public VirtualHostIdProvidersMapping( final Builder builder )
    {
        final Map<IdProviderKey, Set<String>> map = new LinkedHashMap<>();
        if ( builder.defaultIdProvider != null )
        {
            map.put( builder.defaultIdProvider, builder.idProviders.getOrDefault( builder.defaultIdProvider, Set.of() ) );
        }
        map.putAll( builder.idProviders );
        this.idProviders = Collections.unmodifiableMap( map );
    }

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The enabled id providers with their configured flow lists, the default id provider first.
     * An empty flow list means no restriction.
     */
    public Map<IdProviderKey, Set<String>> getIdProviders()
    {
        return idProviders;
    }

    public static class Builder
    {
        private IdProviderKey defaultIdProvider;

        // Insertion-ordered so the (non-default) iteration order is stable.
        private final Map<IdProviderKey, Set<String>> idProviders = new LinkedHashMap<>();

        private Builder()
        {
        }

        public Builder setDefaultIdProvider( final IdProviderKey defaultIdProvider )
        {
            this.defaultIdProvider = defaultIdProvider;
            this.idProviders.putIfAbsent( defaultIdProvider, Set.of() );
            return this;
        }

        public Builder addIdProviderKey( final IdProviderKey idProviderKey )
        {
            this.idProviders.putIfAbsent( idProviderKey, Set.of() );
            return this;
        }

        public Builder addIdProvider( final IdProviderKey idProviderKey, @Nullable final Set<String> flows )
        {
            this.idProviders.put( idProviderKey, flows == null ? Set.of() : Set.copyOf( flows ) );
            return this;
        }

        public VirtualHostIdProvidersMapping build()
        {
            return new VirtualHostIdProvidersMapping( this );
        }
    }
}
