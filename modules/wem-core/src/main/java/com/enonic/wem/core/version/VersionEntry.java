package com.enonic.wem.core.version;

import com.enonic.wem.api.blob.BlobKey;

public class VersionEntry
{
    private final BlobKey blobKey;

    private final BlobKey parent;

    public VersionEntry( final BlobKey blobKey, final BlobKey parent )
    {
        this.blobKey = blobKey;
        this.parent = parent;
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

        final VersionEntry that = (VersionEntry) o;

        if ( blobKey != null ? !blobKey.equals( that.blobKey ) : that.blobKey != null )
        {
            return false;
        }
        if ( parent != null ? !parent.equals( that.parent ) : that.parent != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = blobKey != null ? blobKey.hashCode() : 0;
        result = 31 * result + ( parent != null ? parent.hashCode() : 0 );
        return result;
    }
}
