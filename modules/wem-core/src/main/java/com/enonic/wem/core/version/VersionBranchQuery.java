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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final VersionBranchQuery that = (VersionBranchQuery) o;

        if ( blobKey != null ? !blobKey.equals( that.blobKey ) : that.blobKey != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return blobKey != null ? blobKey.hashCode() : 0;
    }
}
