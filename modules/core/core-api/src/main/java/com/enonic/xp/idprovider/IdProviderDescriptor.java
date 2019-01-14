package com.enonic.xp.idprovider;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.ResourceKey;

public class IdProviderDescriptor
{
    private final ApplicationKey key;

    private final IdProviderDescriptorMode mode;

    private final Form config;

    private IdProviderDescriptor( final Builder builder )
    {
        key = builder.key;
        mode = builder.mode;
        config = builder.config;
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public IdProviderDescriptorMode getMode()
    {
        return mode;
    }

    public Form getConfig()
    {
        return config;
    }

    public ResourceKey getResourceKey()
    {
        return ResourceKey.from( key, "idprovider/idprovider.js" );
    }

    public static ResourceKey toResourceKey( final ApplicationKey key )
    {
        return ResourceKey.from( key, "idprovider/idprovider.xml" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private IdProviderDescriptorMode mode;

        private Form config;

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder mode( final IdProviderDescriptorMode mode )
        {
            this.mode = mode;
            return this;
        }

        public Builder config( final Form config )
        {
            this.config = config;
            return this;
        }

        public IdProviderDescriptor build()
        {
            return new IdProviderDescriptor( this );
        }
    }
}