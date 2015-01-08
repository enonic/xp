package com.enonic.wem.repo.internal.blob.memory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.repo.internal.blob.BlobService;

public final class MockBlobService
    implements BlobService
{
    private final Map<BlobKey, Blob> blobs;

    public MockBlobService()
    {
        this.blobs = Maps.newHashMap();
    }

    @Override
    public Blob create( final InputStream in )
    {
        final MemoryBlob blob = MemoryBlob.create( in );
        this.blobs.put( blob.getKey(), blob );
        return blob;
    }

    @Override
    public Blob get( final BlobKey key )
    {
        return this.blobs.get( key );
    }

    @Override
    public ByteSource getByteSource( final BlobKey blobKey )
    {
        try
        {
            return ByteSource.wrap( ByteStreams.toByteArray( blobs.get( blobKey ).getStream() ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "failed to get ByteSource for blobKey " + blobKey, e );
        }
    }
}
