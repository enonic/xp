package com.enonic.wem.api.entity;

import java.time.Instant;

import com.enonic.wem.api.blob.BlobKey;

public class EntityVersion
    implements Comparable<EntityVersion>
{
    private final BlobKey blobKey;

    private final Instant timestamp;

    public EntityVersion( final BlobKey blobKey, final Instant timestamp )
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


    // Insert with newest first
    @Override
    public int compareTo( final EntityVersion o )
    {
        if ( this.timestamp == o.timestamp )
        {
            return 0;
        }

        if ( this.timestamp.isBefore( o.timestamp ) )
        {
            return 1;
        }

        return -1;
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof EntityVersion ) )
        {
            return false;
        }

        final EntityVersion that = (EntityVersion) o;

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
