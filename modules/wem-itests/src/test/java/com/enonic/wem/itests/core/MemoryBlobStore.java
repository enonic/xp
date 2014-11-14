package com.enonic.wem.itests.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.internal.blob.BlobKeyCreator;
import com.enonic.wem.internal.blob.BlobRecord;
import com.enonic.wem.internal.blob.BlobStore;
import com.enonic.wem.internal.blob.BlobStoreException;
import com.enonic.wem.internal.blob.memory.MemoryBlobRecord;

public class MemoryBlobStore
    implements BlobStore
{
    private final Map<BlobKey, byte[]> blobs = Maps.newHashMap();


    @Override
    public BlobRecord getRecord( final BlobKey key )
        throws BlobStoreException
    {
        if ( blobs.containsKey( key ) )
        {
            return new MemoryBlobRecord( key, blobs.get( key ) );
        }

        return null;
    }

    @Override
    public BlobRecord addRecord( final InputStream in )
        throws BlobStoreException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        final BlobKey key = BlobKeyCreator.createKey( in, outputStream );
        byte[] bytes = outputStream.toByteArray();
        blobs.put( key, bytes );
        IOUtils.closeQuietly( in );
        return new MemoryBlobRecord( key, bytes );
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
        if ( blobs.containsKey( key ) )
        {
            blobs.remove( key );
            return true;
        }

        return false;
    }
}
