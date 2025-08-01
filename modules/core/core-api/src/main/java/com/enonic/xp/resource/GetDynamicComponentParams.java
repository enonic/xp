package com.enonic.xp.resource;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.descriptor.DescriptorKey;

@PublicApi
public final class GetDynamicComponentParams
{
    private final DescriptorKey key;

    private final DynamicComponentType type;

    public GetDynamicComponentParams( final Builder builder )
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

    public DynamicComponentType getType()
    {
        return type;
    }

    public static final class Builder
    {
        private DescriptorKey key;

        private DynamicComponentType type;

        private Builder()
        {
        }

        public Builder descriptorKey( final DescriptorKey key )
        {
            this.key = key;
            return this;
        }

        public Builder type( final DynamicComponentType type )
        {
            this.type = type;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( key, "key must be set" );
            Preconditions.checkNotNull( type, "type must be set" );
        }

        public GetDynamicComponentParams build()
        {
            validate();
            return new GetDynamicComponentParams( this );
        }
    }
}


