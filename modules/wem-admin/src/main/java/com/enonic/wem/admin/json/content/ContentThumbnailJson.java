package com.enonic.wem.admin.json.content;


import com.enonic.wem.api.content.thumb.Thumbnail;

public class ContentThumbnailJson
{
    private final String blobKey;

    private final long size;

    private final String mimeType;

    ContentThumbnailJson( final Thumbnail thumbnail )
    {
        this.blobKey = thumbnail.getBlobKey().toString();
        this.size = thumbnail.getSize();
        this.mimeType = thumbnail.getMimeType();
    }

    public String getBlobKey()
    {
        return blobKey;
    }

    public long getSize()
    {
        return size;
    }

    public String getMimeType()
    {
        return this.mimeType;
    }
}
