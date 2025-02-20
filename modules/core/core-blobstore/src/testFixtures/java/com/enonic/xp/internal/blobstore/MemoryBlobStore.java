package com.enonic.xp.internal.blobstore;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;

public class MemoryBlobStore
    implements BlobStore
{
    private final Map<Segment, Map<BlobKey, BlobRecord>> store = new ConcurrentHashMap<>();

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        final Map<BlobKey, BlobRecord> segmentStore = this.store.get( segment );

        if ( segmentStore == null )
        {
            return null;
        }

        return segmentStore.get( key );
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        final MemoryBlobRecord record = new MemoryBlobRecord( in );

        return doStoreRecord( segment, record.getKey(), record );
    }

    private BlobRecord doStoreRecord( final Segment segment, final BlobKey key, final BlobRecord record )
    {
        this.store.computeIfAbsent( segment, k -> new ConcurrentHashMap<>() ).put( key, record );

        return record;
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        return doStoreRecord( segment, record.getKey(), record );
    }

    @Override
    public void removeRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        final Map<BlobKey, BlobRecord> segmentStore = this.store.get( segment );
        segmentStore.remove( key );
    }

    @Override
    public Stream<BlobRecord> list( final Segment segment )
    {
        final Map<BlobKey, BlobRecord> map = this.store.computeIfAbsent( segment, k -> new ConcurrentHashMap<>() );
        final Collection<BlobRecord> values = map.values();
        return values.stream();
    }

    @Override
    public Stream<Segment> listSegments()
    {
        return store.keySet().stream();
    }

    @Override
    public void deleteSegment( final Segment segment )
        throws BlobStoreException
    {
        store.remove( segment );
    }

    public void clear()
    {
        this.store.clear();
    }
}

