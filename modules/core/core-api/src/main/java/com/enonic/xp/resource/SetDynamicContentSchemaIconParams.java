package com.enonic.xp.resource;

import com.google.common.io.ByteSource;

import com.enonic.xp.schema.BaseSchemaName;

import static java.util.Objects.requireNonNull;

public final class SetDynamicContentSchemaIconParams
{
    private final BaseSchemaName name;

    private final DynamicContentSchemaType type;

    private final ByteSource data;

    private final String mimeType;

    private SetDynamicContentSchemaIconParams( final Builder builder )
    {
        this.name = builder.name;
        this.type = builder.type;
        this.data = builder.data;
        this.mimeType = builder.mimeType;
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

    public ByteSource getData()
    {
        return data;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public static final class Builder
    {
        private BaseSchemaName name;

        private DynamicContentSchemaType type;

        private ByteSource data;

        private String mimeType;

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

        public Builder data( final ByteSource data )
        {
            this.data = data;
            return this;
        }

        public Builder mimeType( final String mimeType )
        {
            this.mimeType = mimeType;
            return this;
        }

        private void validate()
        {
            requireNonNull( name, "name is required" );
            requireNonNull( type, "type is required" );
            requireNonNull( data, "data is required" );
            requireNonNull( mimeType, "mimeType is required" );
        }

        public SetDynamicContentSchemaIconParams build()
        {
            validate();
            return new SetDynamicContentSchemaIconParams( this );
        }
    }
}
