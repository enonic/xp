package com.enonic.xp.resource;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.BaseSchemaName;

@PublicApi
public final class CreateDynamicContentSchemaParams
{
    private final BaseSchemaName name;

    private final DynamicContentSchemaType type;

    private final String resource;

    public CreateDynamicContentSchemaParams( final Builder builder )
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

    public DynamicContentSchemaType getType()
    {
        return type;
    }

    public static final class Builder
    {
        private BaseSchemaName name;

        private DynamicContentSchemaType type;

        private String resource;

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

        public Builder type( final DynamicContentSchemaType type )
        {
            this.type = type;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name must be set" );
            Preconditions.checkNotNull( type, "type must be set" );
        }

        public CreateDynamicContentSchemaParams build()
        {
            validate();
            return new CreateDynamicContentSchemaParams( this );
        }
    }
}


