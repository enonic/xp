package com.enonic.xp.web.vhost.impl.mapping;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

public class VirtualHostIdProvidersMapping
{
    private static final VirtualHostIdProvider UNRESTRICTED = VirtualHostIdProvider.create().build();

    private final Map<IdProviderKey, VirtualHostIdProvider> idProviders;

    public VirtualHostIdProvidersMapping( final Builder builder )
    {
        final Map<IdProviderKey, VirtualHostIdProvider> map = new LinkedHashMap<>();
        if ( builder.defaultIdProvider != null )
        {
            map.put( builder.defaultIdProvider, builder.idProviders.getOrDefault( builder.defaultIdProvider, UNRESTRICTED ) );
        }
        map.putAll( builder.idProviders );
        this.idProviders = Collections.unmodifiableMap( map );
    }

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The enabled id providers with their per-vhost configuration, the default id provider first.
     */
    public Map<IdProviderKey, VirtualHostIdProvider> getIdProviders()
    {
        return idProviders;
    }

    public static class Builder
    {
        private IdProviderKey defaultIdProvider;

        // Insertion-ordered so the (non-default) iteration order is stable.
        private final Map<IdProviderKey, VirtualHostIdProvider> idProviders = new LinkedHashMap<>();

        private Builder()
        {
        }

        public Builder setDefaultIdProvider( final IdProviderKey defaultIdProvider )
        {
            this.defaultIdProvider = defaultIdProvider;
            this.idProviders.putIfAbsent( defaultIdProvider, UNRESTRICTED );
            return this;
        }

        public Builder addIdProviderKey( final IdProviderKey idProviderKey )
        {
            this.idProviders.putIfAbsent( idProviderKey, UNRESTRICTED );
            return this;
        }

        public Builder addIdProvider( final IdProviderKey idProviderKey, @Nullable final Set<String> flows )
        {
            this.idProviders.put( idProviderKey,
                                  flows == null ? UNRESTRICTED : VirtualHostIdProvider.create().flows( flows ).build() );
            return this;
        }

        public VirtualHostIdProvidersMapping build()
        {
            return new VirtualHostIdProvidersMapping( this );
        }
    }
}
