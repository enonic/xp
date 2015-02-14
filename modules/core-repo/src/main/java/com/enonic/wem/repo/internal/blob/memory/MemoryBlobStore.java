package com.enonic.wem.repo.internal.blob.memory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.blob.BlobKey;
import com.enonic.wem.repo.internal.blob.BlobRecord;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.BlobStoreException;

public class MemoryBlobStore
    implements BlobStore
{
    private final Map<BlobKey, MemoryBlob> blobs = Maps.newHashMap();

    @Override
    public BlobRecord getRecord( final BlobKey key )
        throws BlobStoreException
    {
        return null;
    }

    @Override
    public BlobRecord addRecord( final InputStream in )
        throws BlobStoreException
    {
        final MemoryBlob blob = MemoryBlob.create( in );
        this.blobs.put( blob.getKey(), blob );
        return new MemoryBlobRecord( blob.getKey(), blob );
    }

    @Override
    public Iterable<BlobKey> getAllKeys()
        throws BlobStoreException
    {
        return blobs.keySet();
    }

    @Override
    public boolean deleteRecord( final BlobKey key )
        throws BlobStoreException
    {
        return this.blobs.remove( key ) != null;
    }

    @Override
    public ByteSource getByteSource( final BlobKey blobKey )
        throws BlobStoreException
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
