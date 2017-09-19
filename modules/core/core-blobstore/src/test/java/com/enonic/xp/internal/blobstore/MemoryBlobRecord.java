package com.enonic.xp.internal.blobstore;

import java.io.IOException;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;


public class MemoryBlobRecord
    implements BlobRecord
{
    private BlobKey blobKey;

    private ByteSource byteSource;

    private final long lastModified;

    public MemoryBlobRecord( final BlobKey blobKey, final ByteSource source )
    {
        this.blobKey = blobKey;
        this.byteSource = source;
        this.lastModified = System.currentTimeMillis();
    }

    @Override
    public BlobKey getKey()
    {
        return blobKey;
    }

    @Override
    public long getLength()
    {
        try
        {
            return byteSource.size();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "WTF" );
        }
    }

    @Override
    public ByteSource getBytes()
    {
        return byteSource;
    }

    @Override
    public long lastModified()
    {
        return this.lastModified;
    }
}
