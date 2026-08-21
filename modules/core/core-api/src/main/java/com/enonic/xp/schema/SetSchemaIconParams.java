package com.enonic.xp.schema;


import com.google.common.io.ByteSource;

import static java.util.Objects.requireNonNull;


public final class SetSchemaIconParams
{
    private final BaseSchemaName name;

    private final ByteSource data;

    private final String mimeType;

    private SetSchemaIconParams( final Builder builder )
    {
        this.name = builder.name;
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
            requireNonNull( data, "data is required" );
            requireNonNull( mimeType, "mimeType is required" );
        }

        public SetSchemaIconParams build()
        {
            validate();
            return new SetSchemaIconParams( this );
        }
    }
}
