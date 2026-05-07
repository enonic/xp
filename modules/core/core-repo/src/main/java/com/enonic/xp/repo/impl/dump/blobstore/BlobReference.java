package com.enonic.xp.repo.impl.dump.blobstore;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;

import static java.util.Objects.requireNonNull;

@NullMarked
public record BlobReference(Segment segment, BlobKey key)
{
    public BlobReference( final Segment segment, final BlobKey key )
    {
        this.segment = requireNonNull( segment );
        this.key = requireNonNull( key );
    }

    @Override
    public String toString()
    {
        return segment + ":" + key;
    }
}
