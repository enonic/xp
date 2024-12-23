package com.enonic.xp.internal.blobstore;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;


public class MemoryBlobRecord
    implements BlobRecord
{
    private final BlobKey blobKey;

    private final ByteSource byteSource;

    private final long lastModified;

    public MemoryBlobRecord( final ByteSource source )
    {
        this( BlobKey.from( source ), source );
    }

    public MemoryBlobRecord( final BlobKey blobKey, final ByteSource source )
    {
        this.blobKey = blobKey;
        this.lastModified = System.currentTimeMillis();
        try
        {
            this.byteSource = ByteSource.wrap( source.read() );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
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
            throw new UncheckedIOException( e );
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
