package com.enonic.xp.internal.blobstore;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;

public class MemoryBlobStore
    implements BlobStore
{
    private final Map<BlobKey, BlobRecord> store;

    public MemoryBlobStore()
    {
        this.store = Maps.newHashMap();
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        return this.store.get( key );
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        final BlobKey key = BlobKey.from( in );
        final MemoryBlobRecord record = new MemoryBlobRecord( key, in );

        return doStoreRecord( key, record );
    }

    private BlobRecord doStoreRecord( final BlobKey key, final BlobRecord record )
    {
        this.store.put( key, record );

        return record;
    }


    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        return doStoreRecord( record.getKey(), record );
    }
}
