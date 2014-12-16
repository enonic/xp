package com.enonic.wem.admin.json.content;


import com.enonic.wem.api.content.thumb.Thumbnail;

@SuppressWarnings("UnusedDeclaration")
public class ContentThumbnailJson
{
    private final String binaryReference;

    private final long size;

    private final String mimeType;

    ContentThumbnailJson( final Thumbnail thumbnail )
    {
        this.binaryReference = thumbnail.getBinaryReference().toString();
        this.size = thumbnail.getSize();
        this.mimeType = thumbnail.getMimeType();
    }

    public String getBinaryReference()
    {
        return binaryReference;
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
