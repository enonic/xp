package com.enonic.xp.internal.blobstore.readthrough;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SizeBoundedBlobStoreTest
{
    private static final Segment SEGMENT = Segment.from( "test", "blob" );

    private MemoryBlobStore delegate;

    @BeforeEach
    void setUp()
    {
        this.delegate = new MemoryBlobStore();
    }

    private SizeBoundedBlobStore newBoundedStore( final long capacity )
    {
        return new SizeBoundedBlobStore( this.delegate, capacity, Runnable::run, Runnable::run );
    }

    private long delegateTotalSize()
    {
        try (Stream<BlobRecord> records = this.delegate.list( SEGMENT ))
        {
            return records.mapToLong( BlobRecord::getLength ).sum();
        }
    }

    @Test
    void stays_within_capacity()
    {
        final SizeBoundedBlobStore boundedStore = newBoundedStore( 25 );

        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );
        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 2".getBytes() ) );
        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 3".getBytes() ) );
        boundedStore.cleanUp();

        assertTrue( delegateTotalSize() <= 25 );
    }

    @Test
    void within_capacity_keeps_everything()
    {
        final SizeBoundedBlobStore boundedStore = newBoundedStore( 100 );

        final BlobRecord record1 = boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );
        final BlobRecord record2 = boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 2".getBytes() ) );
        boundedStore.cleanUp();

        assertNotNull( this.delegate.getRecord( SEGMENT, record1.getKey() ) );
        assertNotNull( this.delegate.getRecord( SEGMENT, record2.getKey() ) );
    }

    @Test
    void seeds_existing_content()
    {
        this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );
        this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 2".getBytes() ) );
        this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 3".getBytes() ) );

        final SizeBoundedBlobStore boundedStore = newBoundedStore( 25 );
        boundedStore.cleanUp();

        assertTrue( delegateTotalSize() <= 25 );
    }

    @Test
    void get_returns_and_indexes_unindexed_record()
    {
        final SizeBoundedBlobStore boundedStore = newBoundedStore( 100 );

        // added behind the bounded store's back, e.g. leftover from a previous run
        final BlobRecord record = this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );

        final BlobRecord found = boundedStore.getRecord( SEGMENT, record.getKey() );

        assertNotNull( found );
        assertEquals( record.getKey(), found.getKey() );
    }

    @Test
    void getRecord_missing()
    {
        final SizeBoundedBlobStore boundedStore = newBoundedStore( 100 );

        final BlobRecord record = new MemoryBlobStore().addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );

        assertNull( boundedStore.getRecord( SEGMENT, record.getKey() ) );
    }

    @Test
    void removeRecord()
    {
        final SizeBoundedBlobStore boundedStore = newBoundedStore( 100 );

        final BlobRecord record = boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );
        boundedStore.removeRecord( SEGMENT, record.getKey() );

        assertNull( this.delegate.getRecord( SEGMENT, record.getKey() ) );
    }

    @Test
    void deleteSegment()
    {
        final SizeBoundedBlobStore boundedStore = newBoundedStore( 100 );

        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );
        boundedStore.deleteSegment( SEGMENT );

        try (Stream<Segment> segments = this.delegate.listSegments())
        {
            assertEquals( 0, segments.count() );
        }
    }

    @Test
    void no_writes_until_seeded()
    {
        final BlobRecord existing = this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );

        final List<Runnable> pendingSeed = new ArrayList<>();
        final SizeBoundedBlobStore boundedStore = new SizeBoundedBlobStore( this.delegate, 100, Runnable::run, pendingSeed::add );

        // reads are served from the delegate while seeding is pending
        assertNotNull( boundedStore.getRecord( SEGMENT, existing.getKey() ) );

        // writes are not persisted, but the returned record carries the content
        final ByteSource binary = ByteSource.wrap( "10 bytes 2".getBytes() );
        final BlobRecord transientRecord = boundedStore.addRecord( SEGMENT, binary );
        assertEquals( BlobKey.sha256( binary ), transientRecord.getKey() );
        assertEquals( 10, transientRecord.getLength() );
        assertNull( this.delegate.getRecord( SEGMENT, transientRecord.getKey() ) );

        final BlobRecord source = new MemoryBlobStore().addRecord( SEGMENT, ByteSource.wrap( "10 bytes 3".getBytes() ) );
        assertEquals( source, boundedStore.addRecord( SEGMENT, source ) );
        assertNull( this.delegate.getRecord( SEGMENT, source.getKey() ) );

        // seeding completes - writes are stored again
        pendingSeed.forEach( Runnable::run );

        final BlobRecord stored = boundedStore.addRecord( SEGMENT, binary );
        assertNotNull( this.delegate.getRecord( SEGMENT, stored.getKey() ) );
    }
}
