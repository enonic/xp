package com.enonic.xp.blob;

import java.util.stream.Stream;

import com.google.common.io.ByteSource;

public interface BlobStore
{
    BlobRecord getRecord( Segment segment, BlobKey key )
        throws BlobStoreException;

    BlobRecord addRecord( Segment segment, ByteSource in )
        throws BlobStoreException;

    BlobRecord addRecord( Segment segment, BlobRecord record )
        throws BlobStoreException;

    void removeRecord( Segment segment, BlobKey key )
        throws BlobStoreException;

    Stream<BlobRecord> list( Segment segment );

    Stream<Segment> listSegments();

    void deleteSegment( Segment segment );
}
