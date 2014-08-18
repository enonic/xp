package com.enonic.wem.admin.json;


import java.time.Instant;

import com.google.common.hash.Hashing;

import com.enonic.wem.api.Icon;

public class IconJson
{
    private final String mimeType;

    private final int size;

    private final Instant modifiedTime;

    public IconJson( final Icon icon )
    {
        this.mimeType = icon.getMimeType();
        this.size = icon.getSize();
        this.modifiedTime = icon.getModifiedTime();
    }

    public String getMimeType()
    {
        return this.mimeType;
    }

    public int getSize()
    {
        return this.size;
    }

    public Instant getModifiedTime()
    {
        return this.modifiedTime;
    }
}
