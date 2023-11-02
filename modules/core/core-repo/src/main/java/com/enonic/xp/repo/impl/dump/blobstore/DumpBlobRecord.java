package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;

public class DumpBlobRecord
{
    private final BlobReference reference;

    private final FileDumpBlobStore dumpBlobStore;

    public DumpBlobRecord( final Segment segment, final BlobKey key, final FileDumpBlobStore dumpBlobStore )
    {
        this.reference = new BlobReference( segment, key );
        this.dumpBlobStore = dumpBlobStore;
    }

    public BlobKey getKey()
    {
        return reference.getKey();
    }

    public ByteSource getBytes()
    {
        return dumpBlobStore.getBytes( reference );
    }

    public void override( final byte[] bytes )
        throws IOException
    {
        dumpBlobStore.overrideBlob( reference, bytes );
    }
}
