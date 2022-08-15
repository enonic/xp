package com.enonic.xp.resource;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public final class ListDynamicContentSchemasParams
{
    private final ApplicationKey key;

    private final DynamicContentSchemaType type;

    public ListDynamicContentSchemasParams( final Builder builder )
    {
        this.key = builder.key;
        this.type = builder.type;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public DynamicContentSchemaType getType()
    {
        return type;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private DynamicContentSchemaType type;

        public Builder applicationKey( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder type( final DynamicContentSchemaType type )
        {
            this.type = type;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( key, "key must be set" );
            Preconditions.checkNotNull( type, "type must be set" );
        }

        public ListDynamicContentSchemasParams build()
        {
            validate();
            return new ListDynamicContentSchemasParams( this );
        }
    }
}


