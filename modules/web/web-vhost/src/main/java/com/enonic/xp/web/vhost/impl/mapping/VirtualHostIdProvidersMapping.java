package com.enonic.xp.web.vhost.impl.mapping;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.web.vhost.IdProviderFlow;

public class VirtualHostIdProvidersMapping
{
    private final IdProviderKey defaultIdProvider;

    private final ImmutableMap<IdProviderKey, ImmutableSet<IdProviderFlow>> idProviders;

    public VirtualHostIdProvidersMapping( final Builder builder )
    {
        this.defaultIdProvider = builder.defaultIdProvider;
        final ImmutableMap.Builder<IdProviderKey, ImmutableSet<IdProviderFlow>> map = ImmutableMap.builder();
        builder.idProviders.forEach( ( key, flows ) -> map.put( key, ImmutableSet.copyOf( flows ) ) );
        this.idProviders = map.build();
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
     * The flows enabled for the given id provider, or an empty set if it is not enabled here.
     */
    public Set<IdProviderFlow> getFlows( final IdProviderKey idProviderKey )
    {
        return idProviders.getOrDefault( idProviderKey, ImmutableSet.of() );
    }

    public static class Builder
    {
        private IdProviderKey defaultIdProvider;

        // Insertion-ordered so the (non-default) iteration order is stable.
        private final Map<IdProviderKey, Set<IdProviderFlow>> idProviders = new LinkedHashMap<>();

        private Builder()
        {
        }

        public Builder setDefaultIdProvider( final IdProviderKey defaultIdProvider )
        {
            this.defaultIdProvider = defaultIdProvider;
            this.idProviders.computeIfAbsent( defaultIdProvider, k -> EnumSet.allOf( IdProviderFlow.class ) );
            return this;
        }

        public Builder addIdProviderKey( final IdProviderKey idProviderKey )
        {
            this.idProviders.computeIfAbsent( idProviderKey, k -> EnumSet.allOf( IdProviderFlow.class ) );
            return this;
        }

        public Builder addIdProvider( final IdProviderKey idProviderKey, final Set<IdProviderFlow> flows )
        {
            this.idProviders.put( idProviderKey,
                                  flows == null || flows.isEmpty() ? EnumSet.allOf( IdProviderFlow.class ) : EnumSet.copyOf( flows ) );
            return this;
        }

        public VirtualHostIdProvidersMapping build()
        {
            return new VirtualHostIdProvidersMapping( this );
        }
    }
}
