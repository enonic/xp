package com.enonic.xp.schema;

import com.enonic.xp.descriptor.DescriptorKey;

import static java.util.Objects.requireNonNull;


public final class CreateComponentParams
{
    private final DescriptorKey key;

    private final String resource;

    private CreateComponentParams( final Builder builder )
    {
        this.key = builder.key;
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

    public static final class Builder
    {
        private DescriptorKey key;

        private String resource;

        private Builder()
        {
        }

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

        private void validate()
        {
            requireNonNull( key, "key is required" );
            requireNonNull( resource, "resource is required" );
        }

        public CreateComponentParams build()
        {
            validate();
            return new CreateComponentParams( this );
        }
    }
}


