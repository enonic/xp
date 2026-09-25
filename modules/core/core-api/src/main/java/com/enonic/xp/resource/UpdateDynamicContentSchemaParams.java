package com.enonic.xp.resource;

import com.enonic.xp.schema.BaseSchemaName;

import static java.util.Objects.requireNonNull;


public final class UpdateDynamicContentSchemaParams
{
    private final BaseSchemaName name;

    private final String resource;

    private UpdateDynamicContentSchemaParams( final Builder builder )
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
        }

        public UpdateDynamicContentSchemaParams build()
        {
            validate();
            return new UpdateDynamicContentSchemaParams( this );
        }
    }
}


