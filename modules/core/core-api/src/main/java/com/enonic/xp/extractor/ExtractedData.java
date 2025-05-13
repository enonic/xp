package com.enonic.xp.extractor;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ExtractedData
{
    private final Map<String, List<String>> metadata;

    private final String text;

    private final String imageOrientation;

    private ExtractedData( final Builder builder )
    {
        metadata = builder.metadata;
        text = builder.text;
        imageOrientation = builder.imageOrientation;
    }

    public String getImageOrientation()
    {
        return imageOrientation;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String get( final String name )
    {
        final List<String> values = metadata.get( name );

        if ( values == null )
        {
            return null;
        }

        if ( !values.iterator().hasNext() )
        {
            return null;
        }

        return values.iterator().next();
    }

    public Set<String> names()
    {
        return this.metadata.keySet();
    }

    public Map<String, List<String>> getMetadata()
    {
        return metadata;
    }

    public String getText()
    {
        return text;
    }

    public static final class Builder
    {
        private Map<String, List<String>> metadata;

        private String text;

        private String imageOrientation;

        private Builder()
        {
        }

        public Builder metadata( final Map<String, List<String>> val )
        {
            metadata = val;
            return this;
        }

        public Builder text( final String val )
        {
            text = val;
            return this;
        }

        public Builder imageOrientation( final String val )
        {
            imageOrientation = val;
            return this;
        }

        public ExtractedData build()
        {
            return new ExtractedData( this );
        }
    }
}
