package com.enonic.xp.internal.blobstore.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.CachingBlobStore;
import com.enonic.xp.blob.Segment;

public final class CachedBlobStore
    implements BlobStore, CachingBlobStore
{
    private final BlobStore store;

    private final Cache<BlobKey, BlobRecord> cache;

    private long sizeTreshold;

    public CachedBlobStore( final Builder builder )
    {
        this.store = builder.store;
        this.sizeTreshold = builder.sizeTreshold;
        this.cache = CacheBuilder.newBuilder().
            maximumWeight( builder.capacity ).
            weigher( new BlobRecordWeigher() ).
            build();
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        final BlobRecord cached = this.cache.getIfPresent( key );
        if ( cached != null )
        {
            return cached;
        }

        final BlobRecord record = this.store.getRecord( segment, key );
        if ( record == null )
        {
            return null;
        }

        addToCache( record );
        return record;
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        final BlobRecord record = this.store.addRecord( segment, in );
        addToCache( record );
        return record;
    }


    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        this.store.addRecord( segment, record );
        addToCache( record );
        return record;
    }

    @Override
    public void removeRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        this.store.removeRecord( segment, key );
        this.cache.invalidate( key );
    }

    @Override
    public void invalidate( final Segment segment, final BlobKey key )
    {
        this.cache.invalidate( key );
    }

    private void addToCache( final BlobRecord record )
    {
        if ( record.getLength() > this.sizeTreshold )
        {
            return;
        }

        this.cache.put( record.getKey(), new CacheBlobRecord( record.getKey(), record.getBytes() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private BlobStore store;

        private long sizeTreshold = 1000L;

        private long capacity = 10 * 1024L * 1024L;

        public Builder blobStore( final BlobStore store )
        {
            this.store = store;
            return this;
        }

        public Builder sizeTreshold( final long size )
        {
            this.sizeTreshold = size;
            return this;
        }

        public Builder memoryCapacity( final long capacity )
        {
            this.capacity = capacity;
            return this;
        }

        public CachedBlobStore build()
        {
            return new CachedBlobStore( this );
        }
    }
}
