package com.enonic.xp.resource;

import com.google.common.base.Preconditions;

import com.enonic.xp.page.DescriptorKey;

public final class GetDynamicSchemaParams
{
    private final DescriptorKey key;

    private final DynamicSchemaType type;

    public GetDynamicSchemaParams( final Builder builder )
    {
        this.key = builder.key;
        this.type = builder.type;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public DynamicSchemaType getType()
    {
        return type;
    }

    public static final class Builder
    {
        private DescriptorKey key;

        private DynamicSchemaType type;

        public Builder descriptorKey( final DescriptorKey key )
        {
            this.key = key;
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
            Preconditions.checkNotNull( key, "type must be set" );
        }

        public GetDynamicSchemaParams build()
        {
            validate();
            return new GetDynamicSchemaParams( this );
        }
    }
}


