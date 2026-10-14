package com.enonic.xp.resource;

import com.enonic.xp.schema.BaseSchemaName;

import static java.util.Objects.requireNonNull;


public final class CreateDynamicContentSchemaParams
{
    private final BaseSchemaName name;

    private final DynamicContentSchemaType type;

    private final String resource;

    private CreateDynamicContentSchemaParams( final Builder builder )
    {
        this.name = builder.name;
        this.type = builder.type;
        this.resource = builder.resource;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public BaseSchemaName getName()
    {
        return name;
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
    public DynamicContentSchemaType getType()
    {
        return type;
    }

    public static final class Builder
    {
        private BaseSchemaName name;

        private DynamicContentSchemaType type;

        private String resource;

        private Builder()
        {
        }

        public Builder name( final BaseSchemaName name )
        {
            this.name = name;
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
        public Builder type( final DynamicContentSchemaType type )
        {
            this.type = type;
            return this;
        }

        private void validate()
        {
            requireNonNull( name, "name is required" );
        }

        public CreateDynamicContentSchemaParams build()
        {
            validate();
            return new CreateDynamicContentSchemaParams( this );
        }
    }
}


