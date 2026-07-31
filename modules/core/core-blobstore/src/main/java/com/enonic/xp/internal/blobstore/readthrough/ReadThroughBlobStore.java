package com.enonic.xp.internal.blobstore.readthrough;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.CachingBlobStore;
import com.enonic.xp.blob.EvictableBlobStore;
import com.enonic.xp.blob.Segment;

public class ReadThroughBlobStore
    implements BlobStore, CachingBlobStore, EvictableBlobStore
{
    private static final Logger LOG = LoggerFactory.getLogger( ReadThroughBlobStore.class );

    private final BlobStore store;

    private final BlobStore readThroughStore;

    private final long sizeThreshold;

    private final long cacheCapacity;

    private ReadThroughBlobStore( final Builder builder )
    {
        this.sizeThreshold = builder.sizeThreshold;
        this.cacheCapacity = builder.cacheCapacity;
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

        if ( record != null && withinLimit( record ) )
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
        if ( withinLimit( record ) )
        {
            this.readThroughStore.addRecord( segment, record );
        }
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

    @Override
    public Stream<Segment> listSegments()
    {
        return this.store.listSegments();
    }

    @Override
    public void deleteSegment( final Segment segment )
    {
        store.deleteSegment( segment );
        readThroughStore.deleteSegment( segment );
    }

    @Override
    public long evict()
    {
        if ( this.cacheCapacity <= 0 )
        {
            return 0;
        }

        final List<CacheEntry> entries = new ArrayList<>();
        long total = 0;
        long unorderable = 0;

        try (Stream<Segment> segments = this.readThroughStore.listSegments())
        {
            for ( final Segment segment : segments.toList() )
            {
                try (Stream<BlobRecord> records = this.readThroughStore.list( segment ))
                {
                    for ( final BlobRecord record : records.toList() )
                    {
                        final long lastModified = record.lastModified();
                        total += record.getLength();
                        if ( lastModified > 0 )
                        {
                            entries.add( new CacheEntry( segment, record.getKey(), record.getLength(), lastModified ) );
                        }
                        else
                        {
                            unorderable++;
                        }
                    }
                }
            }
        }

        if ( total <= this.cacheCapacity )
        {
            return 0;
        }

        entries.sort( Comparator.comparingLong( CacheEntry::lastModified ) );

        long evicted = 0;
        for ( final CacheEntry entry : entries )
        {
            if ( total <= this.cacheCapacity )
            {
                break;
            }
            this.readThroughStore.removeRecord( entry.segment(), entry.key() );
            total -= entry.length();
            evicted++;
        }

        if ( total > this.cacheCapacity )
        {
            LOG.warn(
                "Could not evict read-through store below capacity [{}]: {} records have no valid last modified time. Automatic cleanup is not supported on filesystems without last modified time support",
                this.cacheCapacity, unorderable );
        }

        return evicted;
    }

    private record CacheEntry(Segment segment, BlobKey key, long length, long lastModified)
    {
    }

    public static final class Builder
    {
        private BlobStore store;

        private BlobStore readThroughStore;

        private long sizeThreshold;

        private long cacheCapacity;

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

        public Builder cacheCapacity( final long cacheCapacity )
        {
            this.cacheCapacity = cacheCapacity;
            return this;
        }

        public ReadThroughBlobStore build()
        {
            return new ReadThroughBlobStore( this );
        }
    }
}
