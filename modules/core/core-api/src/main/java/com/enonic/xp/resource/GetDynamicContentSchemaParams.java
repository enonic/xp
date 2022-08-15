package com.enonic.xp.resource;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.BaseSchemaName;

@PublicApi
public final class GetDynamicContentSchemaParams
{
    private final BaseSchemaName name;

    private final DynamicContentSchemaType type;

    public GetDynamicContentSchemaParams( final Builder builder )
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
            Preconditions.checkNotNull( name, "name must be set" );
            Preconditions.checkNotNull( type, "type must be set" );
        }

        public GetDynamicContentSchemaParams build()
        {
            validate();
            return new GetDynamicContentSchemaParams( this );
        }
    }
}


