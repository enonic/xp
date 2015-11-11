package com.enonic.xp.repo.impl.blob.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.ByteSource;

import com.enonic.xp.repo.impl.blob.BlobKey;
import com.enonic.xp.repo.impl.blob.BlobRecord;
import com.enonic.xp.repo.impl.blob.BlobStore;
import com.enonic.xp.repo.impl.blob.BlobStoreException;

public final class CachedBlobStore
    implements BlobStore
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
    public BlobRecord getRecord( final BlobKey key )
        throws BlobStoreException
    {
        final BlobRecord cached = this.cache.getIfPresent( key );
        if ( cached != null )
        {
            return cached;
        }

        final BlobRecord record = this.store.getRecord( key );
        if ( record == null )
        {
            return null;
        }

        addToCache( record );
        return record;
    }

    @Override
    public BlobRecord addRecord( final ByteSource in )
        throws BlobStoreException
    {
        final BlobRecord record = this.store.addRecord( in );
        addToCache( record );
        return record;
    }

    private void addToCache( final BlobRecord record )
    {
        if ( record.getLength() > this.sizeTreshold )
        {
            return;
        }

        this.cache.put( record.getKey(), record );
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
