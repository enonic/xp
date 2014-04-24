package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.thumb.Thumbnail;

public class ThumbnailJson
{
    private final Thumbnail thumbnail;

    @JsonCreator
    public ThumbnailJson( @JsonProperty("blobKey") final String blobKeyAsString, @JsonProperty("mimeType") final String mimeType,
                          @JsonProperty("size") final String sizeAsString )
    {
        long size = Strings.isNullOrEmpty( sizeAsString ) ? 0 : Long.valueOf( sizeAsString );
        this.thumbnail = Thumbnail.from( new BlobKey( blobKeyAsString ), mimeType, size );
    }

    public String getBlobKey()
    {
        return this.thumbnail.getBlobKey().toString();
    }

    public String getMimeType()
    {
        return this.thumbnail.getMimeType();
    }

    public long getSize()
    {
        return this.thumbnail.getSize();
    }

    @JsonIgnore
    public Thumbnail getThumbnail()
    {
        return thumbnail;
    }
}