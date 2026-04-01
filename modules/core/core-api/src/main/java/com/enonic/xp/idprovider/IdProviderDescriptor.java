package com.enonic.xp.idprovider;

import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.util.GenericValue;

public final class IdProviderDescriptor
{
    private final ApplicationKey key;

    private final IdProviderDescriptorMode mode;

    private final Form config;

    private final GenericValue schemaConfig;

    private IdProviderDescriptor( final Builder builder )
    {
        this.key = builder.key;
        this.mode = builder.mode;
        this.config = Objects.requireNonNullElse( builder.config, Form.empty() );
        this.schemaConfig = builder.schemaConfig.build();
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

    public GenericValue getSchemaConfig()
    {
        return schemaConfig;
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

        private final GenericValue.ObjectBuilder schemaConfig = GenericValue.newObject();

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

        public Builder schemaConfig( final GenericValue value )
        {
            value.properties().forEach( e -> this.schemaConfig.put( e.getKey(), e.getValue() ) );
            return this;
        }

        public IdProviderDescriptor build()
        {
            return new IdProviderDescriptor( this );
        }
    }
}
