package com.enonic.xp.schema;


import com.google.common.io.ByteSource;

import com.enonic.xp.macro.MacroKey;

import static java.util.Objects.requireNonNull;


public final class SetMacroIconParams
{
    private final MacroKey key;

    private final ByteSource data;

    private final String mimeType;

    private SetMacroIconParams( final Builder builder )
    {
        this.key = builder.key;
        this.data = builder.data;
        this.mimeType = builder.mimeType;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public MacroKey getKey()
    {
        return key;
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
        private MacroKey key;

        private ByteSource data;

        private String mimeType;

        private Builder()
        {
        }

        public Builder key( final MacroKey key )
        {
            this.key = key;
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
            requireNonNull( key, "key is required" );
            requireNonNull( data, "data is required" );
            requireNonNull( mimeType, "mimeType is required" );
        }

        public SetMacroIconParams build()
        {
            validate();
            return new SetMacroIconParams( this );
        }
    }
}
