package com.enonic.xp.layer;

import com.google.common.io.ByteSource;

public class GetContentLayerIconResult
{
    private final String mimeType;

    private final String name;

    private final String label;

    private final ByteSource byteSource;

    private GetContentLayerIconResult( final Builder builder )
    {
        mimeType = builder.mimeType;
        name = builder.name;
        label = builder.label;
        byteSource = builder.byteSource;
    }

    public boolean isFound() {
        return byteSource != null;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public ByteSource getByteSource()
    {
        return byteSource;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String mimeType;

        private String name;

        private String label;

        private ByteSource byteSource;

        private Builder()
        {
        }

        public Builder mimeType( final String mimeType )
        {
            this.mimeType = mimeType;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder label( final String label )
        {
            this.label = label;
            return this;
        }

        public Builder byteSource( final ByteSource byteSource )
        {
            this.byteSource = byteSource;
            return this;
        }

        public GetContentLayerIconResult build()
        {
            return new GetContentLayerIconResult( this );
        }
    }
}
