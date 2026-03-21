package com.enonic.xp.repo.impl.dump.blobstore;

import java.util.Objects;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;

public record BlobReference(Segment segment, BlobKey key)
{
    public BlobReference( final Segment segment, final BlobKey key )
    {
        this.segment = Objects.requireNonNull( segment );
        this.key = Objects.requireNonNull( key );
    }

    @Override
    public String toString()
    {
        return segment + ":" + key;
    }
}
