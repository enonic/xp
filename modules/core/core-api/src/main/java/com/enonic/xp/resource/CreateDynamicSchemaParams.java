package com.enonic.xp.resource;

import com.google.common.base.Preconditions;

import com.enonic.xp.page.DescriptorKey;

public final class CreateDynamicSchemaParams
{
    private final DescriptorKey key;

    private final DynamicSchemaType type;

    private final String resource;

    public CreateDynamicSchemaParams( final Builder builder )
    {
        this.key = builder.key;
        this.type = builder.type;
        this.resource = builder.resource;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public String getResource()
    {
        return resource;
    }

    public DynamicSchemaType getType()
    {
        return type;
    }

    public static final class Builder
    {
        private DescriptorKey key;

        private DynamicSchemaType type;

        private String resource;

        public Builder descriptorKey( final DescriptorKey key )
        {
            this.key = key;
            return this;
        }

        public Builder resource( final String resource )
        {
            this.resource = resource;
            return this;
        }

        public Builder type( final DynamicSchemaType type )
        {
            this.type = type;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( key, "key must be set" );
            Preconditions.checkNotNull( type, "type must be set" );
        }

        public CreateDynamicSchemaParams build()
        {
            validate();
            return new CreateDynamicSchemaParams( this );
        }
    }
}


