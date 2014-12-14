package com.enonic.wem.core.media;


import java.util.Map;

import org.apache.tika.mime.MediaType;

import com.google.common.collect.ImmutableMap;

public class MediaInfo
{
    private MediaType mediaType;

    private ImmutableMap<String, String> metadata;

    private MediaInfo( final Builder builder )
    {
        this.mediaType = builder.mediaType;
        this.metadata = builder.metadata.build();

        System.out.println( "MediaInfo: " );
        for ( Map.Entry entry : this.metadata.entrySet() )
        {
            System.out.println( entry.getKey() + " : " + entry.getValue() );
        }
    }

    public MediaType getMediaType()
    {
        return mediaType;
    }

    public ImmutableMap<String, String> getMetadata()
    {
        return metadata;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private MediaType mediaType;

        private ImmutableMap.Builder<String, String> metadata = ImmutableMap.builder();

        public Builder mediaType( final MediaType value )
        {
            this.mediaType = value;
            return this;
        }

        public Builder addMetadata( final String name, final String value )
        {
            this.metadata.put( name, value );
            return this;
        }

        public MediaInfo build()
        {
            return new MediaInfo( this );
        }
    }
}
