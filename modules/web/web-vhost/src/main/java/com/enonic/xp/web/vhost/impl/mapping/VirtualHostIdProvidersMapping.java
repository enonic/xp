package com.enonic.xp.web.vhost.impl.mapping;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;

public class VirtualHostIdProvidersMapping
{
    private final IdProviderKey defaultIdProvider;

    private final IdProviderKeys idProviderKeys;

    public VirtualHostIdProvidersMapping( final Builder builder )
    {
        this.defaultIdProvider = builder.defaultIdProvider;
        this.idProviderKeys = IdProviderKeys.from( builder.idProviderKeys );
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
        return idProviderKeys;
    }

    public static class Builder
    {
        private IdProviderKey defaultIdProvider;

        private List<IdProviderKey> idProviderKeys;

        private Builder()
        {
            this.idProviderKeys = Lists.newArrayList();
        }

        public Builder setDefaultIdProvider( final IdProviderKey defaultIdProvider )
        {
            this.defaultIdProvider = defaultIdProvider;
            addIdProviderKey( defaultIdProvider );

            return this;
        }

        public Builder addIdProviderKey( final IdProviderKey idProviderKey )
        {
            if ( !this.idProviderKeys.contains( idProviderKey ) )
            {
                this.idProviderKeys.add( idProviderKey );
            }

            return this;
        }

        public VirtualHostIdProvidersMapping build()
        {
            return new VirtualHostIdProvidersMapping( this );
        }
    }
}
