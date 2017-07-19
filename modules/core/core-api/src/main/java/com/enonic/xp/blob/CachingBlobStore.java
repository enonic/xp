package com.enonic.xp.blob;

public interface CachingBlobStore
{
    void invalidate( Segment segment, BlobKey key );
}
