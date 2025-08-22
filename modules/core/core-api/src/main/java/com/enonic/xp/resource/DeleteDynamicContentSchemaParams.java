package com.enonic.xp.resource;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.BaseSchemaName;

@PublicApi
public final class DeleteDynamicContentSchemaParams
{
    private final BaseSchemaName name;

    private final DynamicContentSchemaType type;

    private DeleteDynamicContentSchemaParams( final Builder builder )
    {
        this.name = builder.name;
        this.type = builder.type;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public BaseSchemaName getName()
    {
        return name;
    }

    public DynamicContentSchemaType getType()
    {
        return type;
    }

    public static final class Builder
    {
        private BaseSchemaName name;

        private DynamicContentSchemaType type;

        private Builder()
        {
        }

        public Builder name( final BaseSchemaName name )
        {
            this.name = name;
            return this;
        }

        public Builder type( final DynamicContentSchemaType type )
        {
            this.type = type;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( name, "name is required" );
            Objects.requireNonNull( type, "type is required" );
        }

        public DeleteDynamicContentSchemaParams build()
        {
            validate();
            return new DeleteDynamicContentSchemaParams( this );
        }
    }
}


