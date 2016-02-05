package com.enonic.xp.blob;

import com.google.common.io.ByteSource;

public interface BlobStore
{
    BlobRecord getRecord( BlobKey key )
        throws BlobStoreException;

    BlobRecord addRecord( ByteSource in )
        throws BlobStoreException;
}
