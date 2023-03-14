package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;

public class DumpBlobRecord implements BlobRecord
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

    @Override
    public final BlobKey getKey()
    {
        return key;
    }

    @Override
    public ByteSource getBytes()
    {
        return dumpBlobStore.getBytes( segment, key );
    }

    @Override
    public long getLength()
    {
        try
        {
            return dumpBlobStore.getBytes( segment, key ).size();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public long lastModified()
    {
        return 0;
    }

    public ByteSink getByteSink()
    {
        return dumpBlobStore.getByteSink( segment, key );
    }
}
