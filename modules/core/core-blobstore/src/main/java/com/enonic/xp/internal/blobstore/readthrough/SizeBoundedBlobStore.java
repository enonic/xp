package com.enonic.xp.internal.blobstore.readthrough;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;

/**
 * Keeps a delegate blobstore within a total size capacity.
 * <p>
 * Blob bytes stay in the delegate store. A Caffeine cache is used as a size-weighted index over the delegate content:
 * its Window-TinyLFU policy decides, inline on every access, which records are admitted and which are evicted.
 * Evicting an index entry removes the corresponding record from the delegate store.
 * <p>
 * A cache at capacity stays at capacity, so admission matters more than eviction order: a bulk scan cannot flush the
 * working set, because one-time records lose the frequency comparison against established ones and are dropped
 * immediately after being written. No access tracking is persisted and no filesystem timestamp support is needed.
 * <p>
 * On startup the index is seeded from the existing delegate content by a single virtual thread. Until seeding
 * completes, reads are served directly from the delegate but no new records are stored and no accesses are
 * recorded - the store content and the index would otherwise drift apart.
 */
public final class SizeBoundedBlobStore
    implements BlobStore
{
    private static final Logger LOG = LoggerFactory.getLogger( SizeBoundedBlobStore.class );

    private final BlobStore store;

    private final Cache<IndexKey, Long> index;

    private volatile boolean seeded;

    public SizeBoundedBlobStore( final BlobStore store, final long capacity )
    {
        this( store, capacity, null, task -> Thread.ofVirtual().name( "blobstore-cache-seed" ).start( task ) );
    }

    SizeBoundedBlobStore( final BlobStore store, final long capacity, final Executor indexExecutor, final Executor seedExecutor )
    {
        this.store = store;

        final Caffeine<IndexKey, Long> builder = Caffeine.newBuilder()
            .maximumWeight( capacity )
            .weigher( ( IndexKey key, Long length ) -> (int) Math.min( length, Integer.MAX_VALUE ) )
            .evictionListener( ( key, length, cause ) -> removeEvicted( key ) );
        if ( indexExecutor != null )
        {
            builder.executor( indexExecutor );
        }
        this.index = builder.build();

        seedExecutor.execute( this::seed );
    }

    private void seed()
    {
        final long start = System.nanoTime();
        long count = 0;
        try
        {
            final List<Segment> segments;
            try (Stream<Segment> segmentStream = this.store.listSegments())
            {
                segments = segmentStream.toList();
            }
            for ( final Segment segment : segments )
            {
                try (Stream<BlobRecord> records = this.store.list( segment ))
                {
                    for ( final Iterator<BlobRecord> it = records.iterator(); it.hasNext(); )
                    {
                        final BlobRecord record = it.next();
                        this.index.put( new IndexKey( segment, record.getKey() ), record.getLength() );
                        count++;
                    }
                }
            }
            LOG.info( "Indexed {} existing blobs in {} ms", count, ( System.nanoTime() - start ) / 1_000_000 );
        }
        catch ( Exception e )
        {
            LOG.warn( "Failed to index existing blobs", e );
        }
        finally
        {
            this.seeded = true;
        }
    }

    private void removeEvicted( final IndexKey key )
    {
        try
        {
            this.store.removeRecord( key.segment(), key.key() );
        }
        catch ( Exception e )
        {
            LOG.debug( "Failed to remove evicted blob [{}]", key.key(), e );
        }
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        if ( !this.seeded )
        {
            return this.store.getRecord( segment, key );
        }

        // records the access in the admission filter, also when the record is not present yet
        final Long indexed = this.index.getIfPresent( new IndexKey( segment, key ) );

        final BlobRecord record = this.store.getRecord( segment, key );

        if ( record != null && indexed == null )
        {
            this.index.put( new IndexKey( segment, key ), record.getLength() );
        }

        return record;
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        if ( !this.seeded )
        {
            return new TransientBlobRecord( in );
        }

        final BlobRecord record = this.store.addRecord( segment, in );
        this.index.put( new IndexKey( segment, record.getKey() ), record.getLength() );
        return record;
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        if ( !this.seeded )
        {
            return record;
        }

        final BlobRecord added = this.store.addRecord( segment, record );
        this.index.put( new IndexKey( segment, added.getKey() ), added.getLength() );
        return added;
    }

    @Override
    public void removeRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        this.index.invalidate( new IndexKey( segment, key ) );
        this.store.removeRecord( segment, key );
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
        this.index.asMap().keySet().removeIf( key -> key.segment().equals( segment ) );
        this.store.deleteSegment( segment );
    }

    void cleanUp()
    {
        this.index.cleanUp();
    }

    private record IndexKey(Segment segment, BlobKey key)
    {
    }

    /**
     * Returned instead of storing while the index seeding is in progress. The record carries the content,
     * so callers can still read it, but nothing is persisted in the cache store.
     */
    private static final class TransientBlobRecord
        implements BlobRecord
    {
        private final BlobKey key;

        private final ByteSource bytes;

        TransientBlobRecord( final ByteSource bytes )
        {
            this.key = BlobKey.sha256( bytes );
            this.bytes = bytes;
        }

        @Override
        public BlobKey getKey()
        {
            return this.key;
        }

        @Override
        public long getLength()
        {
            try
            {
                return this.bytes.size();
            }
            catch ( IOException e )
            {
                throw new BlobStoreException( "Failed to get blob size", e );
            }
        }

        @Override
        public ByteSource getBytes()
        {
            return this.bytes;
        }

        @Override
        public long lastModified()
        {
            return System.currentTimeMillis();
        }
    }
}
