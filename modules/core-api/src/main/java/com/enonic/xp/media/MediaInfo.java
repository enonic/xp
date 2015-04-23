package com.enonic.xp.media;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.form.FormItemName;

@Beta
public class MediaInfo
{
    private String mediaType;

    private Multimap<String, String> metadata;

    public static final String MEDIA_SOURCE_SIZE = "bytesize";

    public static final String BINARY_FILE_MIME = "application/octet-stream";

    private MediaInfo( final Builder builder )
    {
        this.mediaType = builder.mediaType;
        this.metadata = builder.metadata.build();
        Preconditions.checkNotNull( this.metadata, "metadata cannot be null" );
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public Multimap<String, String> getMetadata()
    {
        return metadata;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String mediaType;

        private ImmutableMultimap.Builder<String, String> metadata = ImmutableMultimap.builder();

        public Builder mediaType( final String value )
        {
            this.mediaType = value;
            return this;
        }

        public Builder addMetadata( final String name, final String value )
        {
            this.metadata.put( FormItemName.safeName( name ), value );
            return this;
        }

        public MediaInfo build()
        {
            return new MediaInfo( this );
        }
    }
}
