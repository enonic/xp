package com.enonic.wem.core.version;

import com.enonic.wem.api.blob.BlobKey;

public class VersionEntry
{
    private BlobKey blobKey;

    private BlobKey parent;

    public VersionEntry( final BlobKey blobKey, final BlobKey parent )
    {
        this.blobKey = blobKey;
        this.parent = parent;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }

    public BlobKey getParent()
    {
        return parent;
    }
}
