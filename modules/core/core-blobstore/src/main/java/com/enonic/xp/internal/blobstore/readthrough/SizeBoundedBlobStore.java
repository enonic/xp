package com.enonic.xp.internal.blobstore.readthrough;

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
 * completes, reads and writes pass straight through to the delegate without touching the index: no accesses are
 * recorded and no capacity is enforced. Records written while seeding runs are picked up by the seeding walk
 * itself or, failing that, indexed on their first read after seeding.
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
            LOG.debug( "Indexed {} existing blobs in {} ms", count, ( System.nanoTime() - start ) / 1_000_000 );
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
        final BlobRecord record = this.store.addRecord( segment, in );
        if ( this.seeded )
        {
            this.index.put( new IndexKey( segment, record.getKey() ), record.getLength() );
        }
        return record;
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        final BlobRecord added = this.store.addRecord( segment, record );
        if ( this.seeded )
        {
            this.index.put( new IndexKey( segment, added.getKey() ), added.getLength() );
        }
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
}
