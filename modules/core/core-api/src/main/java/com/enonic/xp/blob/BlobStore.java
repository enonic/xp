package com.enonic.xp.blob;

import com.google.common.io.ByteSource;

public interface BlobStore
{
    BlobRecord getRecord( Segment segment, BlobKey key )
        throws BlobStoreException;

    BlobRecord addRecord( Segment segment, ByteSource in )
        throws BlobStoreException;

    BlobRecord addRecord( Segment segment, BlobRecord record )
        throws BlobStoreException;
}
