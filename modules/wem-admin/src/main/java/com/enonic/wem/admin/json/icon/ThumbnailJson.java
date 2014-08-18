package com.enonic.wem.admin.json.icon;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.thumb.Thumbnail;

public class ThumbnailJson
{
    private final Thumbnail thumbnail;

    @JsonIgnore
    public ThumbnailJson( final Thumbnail thumbnail )
    {
        this.thumbnail = thumbnail;
    }

    @JsonCreator
    public ThumbnailJson( @JsonProperty("blobKey") final String blobKey, @JsonProperty("mimeType") final String mimeType )
    {
        this.thumbnail = Thumbnail.from( new BlobKey( blobKey ), mimeType, 0 );
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
