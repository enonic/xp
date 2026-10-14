package com.enonic.xp.resource;

import com.enonic.xp.descriptor.DescriptorKey;

import static java.util.Objects.requireNonNull;


public final class UpdateDynamicComponentParams
{
    private final DescriptorKey key;

    private final DynamicComponentType type;

    private final String resource;

    private UpdateDynamicComponentParams( final Builder builder )
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

    /**
     * @deprecated the kind of the schema is told by the service method called, the type is only read by the deprecated generic
     * methods of {@link DynamicSchemaService}
     */
    @Deprecated
    public DynamicComponentType getType()
    {
        return type;
    }

    public static final class Builder
    {
        private DescriptorKey key;

        private DynamicComponentType type;

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

        /**
         * @deprecated only the deprecated generic methods of {@link DynamicSchemaService} read it
         */
        @Deprecated
        public Builder type( final DynamicComponentType type )
        {
            this.type = type;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
        }

        public UpdateDynamicComponentParams build()
        {
            validate();
            return new UpdateDynamicComponentParams( this );
        }
    }
}


