package com.enonic.wem.core.version;

import java.time.Instant;

import com.enonic.wem.api.blob.BlobKey;

public class VersionEntry
    implements Comparable<VersionEntry>
{
    private final BlobKey blobKey;

    private final Instant timestamp;

    public VersionEntry( final BlobKey blobKey, final Instant timestamp )
    {
        this.blobKey = blobKey;
        this.timestamp = timestamp;
    }

    public BlobKey getBlobKey()
    {
        return blobKey;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    @Override
    public int compareTo( final VersionEntry o )
    {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than
        // other and 0 if they are supposed to be equal

        if ( this.timestamp == o.timestamp )
        {
            return 0;
        }

        if ( this.timestamp.isBefore( o.timestamp ) )
        {
            return -1;
        }

        return 1;
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof VersionEntry ) )
        {
            return false;
        }

        final VersionEntry that = (VersionEntry) o;

        if ( blobKey != null ? !blobKey.equals( that.blobKey ) : that.blobKey != null )
        {
            return false;
        }
        if ( timestamp != null ? !timestamp.equals( that.timestamp ) : that.timestamp != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = blobKey != null ? blobKey.hashCode() : 0;
        result = 31 * result + ( timestamp != null ? timestamp.hashCode() : 0 );
        return result;
    }
}
