package com.enonic.xp.web.vhost.impl.mapping;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;

public class VirtualHostIdProvidersMapping
{
    private final IdProviderKeys idProviderKeys;

    public VirtualHostIdProvidersMapping( final Builder builder )
    {
        this.idProviderKeys = IdProviderKeys.from(
            ImmutableSet.<IdProviderKey>builder().add( builder.defaultIdProvider ).addAll( builder.idProviderKeys.build() ).build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IdProviderKey getDefaultIdProvider()
    {
        return idProviderKeys.first();
    }

    public IdProviderKeys getIdProviderKeys()
    {
        return idProviderKeys;
    }

    public static class Builder
    {
        private IdProviderKey defaultIdProvider;

        private final ImmutableSet.Builder<IdProviderKey> idProviderKeys = ImmutableSet.builder();

        private Builder()
        {
        }

        public Builder setDefaultIdProvider( final IdProviderKey defaultIdProvider )
        {
            this.defaultIdProvider = defaultIdProvider;
            return this;
        }

        public Builder addIdProviderKey( final IdProviderKey idProviderKey )
        {
            this.idProviderKeys.add( idProviderKey );
            return this;
        }

        public VirtualHostIdProvidersMapping build()
        {
            return new VirtualHostIdProvidersMapping( this );
        }
    }
}
