package com.enonic.xp.core.content;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;

public class MemoryBlobStore
    implements BlobStore
{
    final Map<BlobKey, BlobRecord> store;

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
        final BlobKey key = createKey( in );

        final MemoryBlobRecord record = new MemoryBlobRecord( key, in );

        this.store.put( key, record );

        return record;
    }


    public BlobKey createKey( final ByteSource in )
    {
        try
        {
            return new BlobKey( in.hash( Hashing.sha1() ).asBytes() );
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to create blobKey", e );
        }
    }
}
