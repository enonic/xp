package com.enonic.xp.repo.impl.dump.blobstore;

import java.util.Objects;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;

public final class BlobReference
{
    private final Segment segment;

    private final BlobKey key;

    public BlobReference( final Segment segment, final BlobKey key )
    {
        this.segment = segment;
        this.key = key;
    }

    public Segment getSegment()
    {
        return segment;
    }

    public BlobKey getKey()
    {
        return key;
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
        final BlobReference that = (BlobReference) o;
        return Objects.equals( segment, that.segment ) && Objects.equals( key, that.key );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( segment, key );
    }

    @Override
    public String toString()
    {
        return segment + ":" + key;
    }
}
