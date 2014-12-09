package com.enonic.wem.api.util;

import com.enonic.wem.api.blob.BlobKey;

public class Binary
{
    private final String name;

    private final String mimeType;

    private final BlobKey blobKey;

    private Binary( final String name, final String mimeType, final BlobKey blobKey )
    {
        this.name = name;
        this.mimeType = mimeType;
        this.blobKey = blobKey;
    }

    public static Binary from( final String value )
    {
        // TODO: deserialize
        return Binary.from( value, null, null );
    }

    public static Binary from( final String name, final String mimeType, final BlobKey blobKey )
    {
        return new Binary( name, mimeType, blobKey );
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}


