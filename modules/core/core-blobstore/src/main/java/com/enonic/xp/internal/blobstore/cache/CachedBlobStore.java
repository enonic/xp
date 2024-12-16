package com.enonic.xp.internal.blobstore.cache;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
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
    private static final Logger LOG = LoggerFactory.getLogger( CachedBlobStore.class );

    private final BlobStore store;

    private final Cache<BlobKey, CacheBlobRecord> cache;

    private final long sizeThreshold;

    private CachedBlobStore( final Builder builder )
    {
        this.store = builder.store;
        this.sizeThreshold = builder.sizeThreshold;
        this.cache = CacheBuilder.newBuilder().maximumWeight( builder.capacity ).weigher( BlobRecordWeigher.INSTANCE ).build();
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

        return addToCache( record );
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        final BlobRecord record = this.store.addRecord( segment, in );
        return addToCache( record );
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        this.store.addRecord( segment, record );
        return addToCache( record );
    }

    @Override
    public void removeRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        this.store.removeRecord( segment, key );
    }

    @Override
    public void invalidate( final Segment segment, final BlobKey key )
    {
        this.cache.invalidate( key );
    }

    private BlobRecord addToCache( final BlobRecord record )
    {
        // Quick check to avoid caching large blobs
        if ( record.getLength() > this.sizeThreshold )
        {
            return record;
        }

        // Don't do heavy lifting if the blob is already in the cache
        final BlobKey key = record.getKey();
        final CacheBlobRecord present = this.cache.getIfPresent( key );
        if ( present != null )
        {
            return present;
        }

        // We don't want return blobs that do not actually reside in cache (for instance record gets evacuated immediately after being added)
        try
        {
            this.cache.put( key, new CacheBlobRecord( record ) );
        }
        catch ( IOException e )
        {
            // blobs that could not be cached should be just returned as-is
            LOG.error( "Could not create cache blob-record", e );
        }

        return Objects.requireNonNullElse( this.cache.getIfPresent( key ), record );
    }

    @Override
    public Stream<BlobRecord> list( final Segment segment )
    {
        return this.store.list( segment );
    }

    @Override
    public Stream<Segment> listSegments()
    {
        return this.store.listSegments();
    }

    @Override
    public void deleteSegment( final Segment segment )
    {
        store.deleteSegment( segment );
        cache.invalidateAll();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private BlobStore store;

        private long sizeThreshold = 1024L;

        private long capacity = 10 * 1024L * 1024L;

        public Builder blobStore( final BlobStore store )
        {
            this.store = store;
            return this;
        }

        public Builder sizeThreshold( final long size )
        {
            this.sizeThreshold = size;
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

    private enum BlobRecordWeigher
        implements Weigher<BlobKey, BlobRecord>
    {
        INSTANCE;

        @Override
        public int weigh( final BlobKey key, final BlobRecord record )
        {
            return (int) Math.min( record.getLength(), Integer.MAX_VALUE );
        }
    }
}
