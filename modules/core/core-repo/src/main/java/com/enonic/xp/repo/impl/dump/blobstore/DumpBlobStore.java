package com.enonic.xp.repo.impl.dump.blobstore;

import com.google.common.io.ByteSource;

public interface DumpBlobStore
{
    ByteSource getBytes( BlobReference reference );

    void addRecord( BlobReference blobContainer );
}
