package com.enonic.xp.internal.blobstore.readthrough;

import java.util.stream.Stream;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.CachingBlobStore;
import com.enonic.xp.blob.Segment;

public class ReadThroughBlobStore
    implements BlobStore, CachingBlobStore
{
    private BlobStore store;

    private BlobStore readThroughStore;

    private long sizeThreshold;

    private ReadThroughBlobStore( final Builder builder )
    {
        this.sizeThreshold = builder.sizeThreshold;
        this.store = builder.store;
        this.readThroughStore = builder.readThroughStore;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        BlobRecord record = this.readThroughStore.getRecord( segment, key );

        if ( record != null )
        {
            return record;
        }

        record = store.getRecord( segment, key );

        if ( record != null )
        {
            this.readThroughStore.addRecord( segment, record );
        }

        return record;
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        final BlobRecord blobRecord = this.store.addRecord( segment, in );

        if ( withinLimit( blobRecord ) )
        {
            this.readThroughStore.addRecord( segment, blobRecord );
        }

        return blobRecord;
    }

    private boolean withinLimit( final BlobRecord blobRecord )
    {
        return blobRecord.getLength() <= this.sizeThreshold;
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        this.store.addRecord( segment, record );
        this.readThroughStore.addRecord( segment, record );
        return record;
    }

    @Override
    public void removeRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        this.store.removeRecord( segment, key );
        this.invalidate( segment, key );
    }

    @Override
    public void invalidate( final Segment segment, final BlobKey key )
    {
        readThroughStore.removeRecord( segment, key );

        if ( store instanceof CachingBlobStore )
        {
            ( (CachingBlobStore) store ).invalidate( segment, key );
        }
    }

    @Override
    public Stream<BlobRecord> list( final Segment segment )
    {
        return this.store.list( segment );
    }

    public static final class Builder
    {
        private BlobStore store;

        private BlobStore readThroughStore;

        private long sizeThreshold;

        private Builder()
        {
        }

        public Builder store( final BlobStore val )
        {
            store = val;
            return this;
        }

        public Builder readThroughStore( final BlobStore val )
        {
            readThroughStore = val;
            return this;
        }

        public Builder sizeThreshold( final long sizeThreshold )
        {
            this.sizeThreshold = sizeThreshold;
            return this;
        }

        public ReadThroughBlobStore build()
        {
            return new ReadThroughBlobStore( this );
        }
    }
}
