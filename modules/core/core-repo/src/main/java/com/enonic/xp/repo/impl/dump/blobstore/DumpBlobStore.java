package com.enonic.xp.repo.impl.dump.blobstore;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;

public interface DumpBlobStore
{
    DumpBlobRecord getRecord( Segment segment, BlobKey key );

    void addRecord( BlobContainer blobContainer );
}
