package com.enonic.xp.resource;

import com.google.common.io.ByteSource;

import com.enonic.xp.descriptor.DescriptorKey;

import static java.util.Objects.requireNonNull;

public final class SetDynamicComponentIconParams
{
    private final DescriptorKey key;

    private final DynamicComponentType type;

    private final ByteSource data;

    private final String mimeType;

    private SetDynamicComponentIconParams( final Builder builder )
    {
        this.key = builder.key;
        this.type = builder.type;
        this.data = builder.data;
        this.mimeType = builder.mimeType;
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

    public ByteSource getData()
    {
        return data;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public static final class Builder
    {
        private DescriptorKey key;

        private DynamicComponentType type;

        private ByteSource data;

        private String mimeType;

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

        public Builder data( final ByteSource data )
        {
            this.data = data;
            return this;
        }

        public Builder mimeType( final String mimeType )
        {
            this.mimeType = mimeType;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
            requireNonNull( type, "type is required" );
            requireNonNull( data, "data is required" );
            requireNonNull( mimeType, "mimeType is required" );
        }

        public SetDynamicComponentIconParams build()
        {
            validate();
            return new SetDynamicComponentIconParams( this );
        }
    }
}
