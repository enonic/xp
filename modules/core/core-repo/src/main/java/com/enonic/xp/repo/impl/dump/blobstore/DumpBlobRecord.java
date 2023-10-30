package com.enonic.xp.repo.impl.dump.blobstore;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;

public class DumpBlobRecord
{
    private final BlobReference reference;

    private final AbstractDumpBlobStore dumpBlobStore;

    public DumpBlobRecord( final Segment segment, final BlobKey key, final AbstractDumpBlobStore dumpBlobStore )
    {
        this.reference = new BlobReference( segment, key );
        this.dumpBlobStore = dumpBlobStore;
    }

    public final BlobKey getKey()
    {
        return reference.getKey();
    }

    public ByteSource getBytes()
    {
        return dumpBlobStore.getBytes( reference );
    }

    public ByteSink getByteSink()
    {
        return dumpBlobStore.getByteSink( reference );
    }
}
