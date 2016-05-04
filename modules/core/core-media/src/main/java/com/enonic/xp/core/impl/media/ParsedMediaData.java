package com.enonic.xp.core.impl.media;

import org.apache.tika.metadata.Metadata;


public class ParsedMediaData
{
    private final Metadata metadata;

    private final String textContent;

    private ParsedMediaData( final Builder builder )
    {
        metadata = builder.metadata;
        textContent = builder.content;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Metadata getMetadata()
    {
        return metadata;
    }

    public String getTextContent()
    {
        return textContent;
    }

    public static final class Builder
    {
        private Metadata metadata;

        private String content;

        private Builder()
        {
        }

        public Builder metadata( final Metadata val )
        {
            metadata = val;
            return this;
        }

        public Builder handler( final String val )
        {
            content = val;
            return this;
        }

        public ParsedMediaData build()
        {
            return new ParsedMediaData( this );
        }
    }
}
