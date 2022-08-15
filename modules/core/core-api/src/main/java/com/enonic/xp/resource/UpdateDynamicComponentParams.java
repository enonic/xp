package com.enonic.xp.resource;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public final class UpdateDynamicComponentParams
{
    private final DescriptorKey key;

    private final DynamicComponentType type;

    private final String resource;

    public UpdateDynamicComponentParams( final Builder builder )
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

    public DynamicComponentType getType()
    {
        return type;
    }

    public static final class Builder
    {
        private DescriptorKey key;

        private DynamicComponentType type;

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

        public UpdateDynamicComponentParams build()
        {
            validate();
            return new UpdateDynamicComponentParams( this );
        }
    }
}


