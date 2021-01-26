package com.enonic.xp.repo.impl.dump.blobstore;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;

public interface DumpBlobStore
{
    DumpBlobRecord getRecord( Segment segment, BlobKey key );

    BlobKey addRecord( Segment segment, ByteSource in );
}
