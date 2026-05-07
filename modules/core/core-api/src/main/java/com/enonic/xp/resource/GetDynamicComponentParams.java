package com.enonic.xp.resource;

import com.enonic.xp.descriptor.DescriptorKey;

import static java.util.Objects.requireNonNull;


public final class GetDynamicComponentParams
{
    private final DescriptorKey key;

    private final DynamicComponentType type;

    private GetDynamicComponentParams( final Builder builder )
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
            requireNonNull( key, "key is required" );
            requireNonNull( type, "type is required" );
        }

        public GetDynamicComponentParams build()
        {
            validate();
            return new GetDynamicComponentParams( this );
        }
    }
}


