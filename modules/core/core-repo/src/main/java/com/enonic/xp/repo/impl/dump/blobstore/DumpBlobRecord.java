package com.enonic.xp.repo.impl.dump.blobstore;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;

public class DumpBlobRecord
{
    private final BlobKey key;

    private final Segment segment;

    private final AbstractDumpBlobStore dumpBlobStore;

    public DumpBlobRecord( final Segment segment, final BlobKey key, final AbstractDumpBlobStore dumpBlobStore )
    {
        this.key = key;
        this.segment = segment;
        this.dumpBlobStore = dumpBlobStore;
    }

    public final BlobKey getKey()
    {
        return key;
    }

    public ByteSource getBytes()
    {
        return dumpBlobStore.getBytes( segment, key );
    }

    public ByteSink getByteSink()
    {
        return dumpBlobStore.getByteSink( segment, key );
    }
}
