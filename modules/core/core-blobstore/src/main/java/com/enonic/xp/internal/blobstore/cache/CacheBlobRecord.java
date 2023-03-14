package com.enonic.xp.internal.blobstore.cache;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;

public class CacheBlobRecord
    implements BlobRecord
{
    private final BlobKey blobKey;

    private final long lastModified;

    private final ByteSource content;

    public CacheBlobRecord( final BlobRecord blobRecord )
        throws IOException
    {
        this.blobKey = blobRecord.getKey();
        this.content = ByteSource.wrap( blobRecord.getBytes().read() );
        this.lastModified = blobRecord.lastModified();
    }

    @Override
    public long lastModified()
    {
        return this.lastModified;
    }

    @Override
    public BlobKey getKey()
    {
        return this.blobKey;
    }

    @Override
    public long getLength()
    {
        try
        {
            return content.size();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public ByteSource getBytes()
    {
        return content;
    }
}
