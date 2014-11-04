package com.enonic.wem.itests.core.entity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.carrotsearch.ant.tasks.junit4.dependencies.com.google.common.collect.Maps;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.core.blob.BlobKeyCreator;
import com.enonic.wem.core.blob.BlobRecord;
import com.enonic.wem.core.blob.BlobStore;
import com.enonic.wem.core.blob.BlobStoreException;
import com.enonic.wem.core.blob.memory.MemoryBlobRecord;

class MemoryBlobStore
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
