package com.enonic.wem.core.version;

import com.enonic.wem.api.blob.BlobKey;

public class VersionBranchQuery
{
    private final BlobKey blobKey;

    public VersionBranchQuery( final BlobKey blobKey )
    {
        this.blobKey = blobKey;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }
}
