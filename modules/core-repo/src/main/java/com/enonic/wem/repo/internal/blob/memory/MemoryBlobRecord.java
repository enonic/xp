package com.enonic.wem.repo.internal.blob.memory;

import java.io.InputStream;

import com.enonic.xp.blob.BlobKey;
import com.enonic.wem.repo.internal.blob.BlobRecord;
import com.enonic.wem.repo.internal.blob.BlobStoreException;

public class MemoryBlobRecord
    extends BlobRecord
{
    final MemoryBlob memoryBlob;

    public MemoryBlobRecord( final BlobKey key, final MemoryBlob blob )
    {
        super( key );
        this.memoryBlob = blob;
    }

    @Override
    public long getLength()
    {
        return memoryBlob.getLength();
    }

    @Override
    public InputStream getStream()
        throws BlobStoreException
    {
        return this.memoryBlob.getStream();
    }
}
