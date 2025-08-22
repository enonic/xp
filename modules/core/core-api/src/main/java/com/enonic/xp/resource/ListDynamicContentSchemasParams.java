package com.enonic.xp.resource;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public final class ListDynamicContentSchemasParams
{
    private final ApplicationKey key;

    private final DynamicContentSchemaType type;

    private ListDynamicContentSchemasParams( final Builder builder )
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

        private Builder()
        {
        }

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
            Objects.requireNonNull( key, "key is required" );
            Objects.requireNonNull( type, "type is required" );
        }

        public ListDynamicContentSchemasParams build()
        {
            validate();
            return new ListDynamicContentSchemasParams( this );
        }
    }
}


