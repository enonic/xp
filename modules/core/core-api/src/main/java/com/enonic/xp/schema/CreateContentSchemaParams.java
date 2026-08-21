package com.enonic.xp.schema;


import static java.util.Objects.requireNonNull;


public final class CreateContentSchemaParams
{
    private final BaseSchemaName name;

    private final String resource;

    private CreateContentSchemaParams( final Builder builder )
    {
        this.name = builder.name;
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

    public static final class Builder
    {
        private BaseSchemaName name;

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

        private void validate()
        {
            requireNonNull( name, "name is required" );
            requireNonNull( resource, "resource is required" );
        }

        public CreateContentSchemaParams build()
        {
            validate();
            return new CreateContentSchemaParams( this );
        }
    }
}


