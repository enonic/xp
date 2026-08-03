package com.enonic.xp.internal.blobstore.readthrough;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStoreException;
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
    void seeding_retries_on_failure()
    {
        this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );
        this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 2".getBytes() ) );
        this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 3".getBytes() ) );

        final MemoryBlobStore flaky = Mockito.spy( this.delegate );
        Mockito.doThrow( new BlobStoreException( "transient failure" ) ).doCallRealMethod().when( flaky ).listSegments();

        final SizeBoundedBlobStore boundedStore = new SizeBoundedBlobStore( flaky, 25, Runnable::run, Runnable::run );
        boundedStore.cleanUp();

        assertTrue( delegateTotalSize() <= 25 );
    }

    @Test
    void seeding_failure_still_enforces_capacity_for_new_records()
    {
        final MemoryBlobStore failing = Mockito.spy( this.delegate );
        Mockito.doThrow( new BlobStoreException( "permanent failure" ) ).when( failing ).listSegments();

        final SizeBoundedBlobStore boundedStore = new SizeBoundedBlobStore( failing, 25, Runnable::run, Runnable::run );

        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );
        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 2".getBytes() ) );
        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 3".getBytes() ) );
        boundedStore.cleanUp();

        assertTrue( delegateTotalSize() <= 25 );
    }

    @Test
    void seeding_interrupted_still_enforces_capacity_for_new_records()
        throws Exception
    {
        final MemoryBlobStore failing = Mockito.spy( this.delegate );
        Mockito.doThrow( new BlobStoreException( "failure" ) ).when( failing ).listSegments();

        final List<Runnable> seedTask = new ArrayList<>();
        final SizeBoundedBlobStore boundedStore =
            new SizeBoundedBlobStore( failing, 25, Runnable::run, seedTask::add, Duration.ofDays( 1 ) );

        final Thread seeder = new Thread( seedTask.get( 0 ) );
        seeder.start();
        seeder.interrupt();
        seeder.join( 10_000 );

        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );
        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 2".getBytes() ) );
        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 3".getBytes() ) );
        boundedStore.cleanUp();

        assertTrue( delegateTotalSize() <= 25 );
    }

    @Test
    void eviction_removal_failure_is_tolerated()
    {
        final MemoryBlobStore failing = Mockito.spy( this.delegate );
        Mockito.doThrow( new BlobStoreException( "no removal" ) ).when( failing ).removeRecord( Mockito.any(), Mockito.any() );

        final SizeBoundedBlobStore boundedStore = new SizeBoundedBlobStore( failing, 25, Runnable::run, Runnable::run );

        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );
        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 2".getBytes() ) );
        boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 3".getBytes() ) );
        boundedStore.cleanUp();

        // records stay in the delegate because removal fails, but no exception surfaces
        assertEquals( 30, delegateTotalSize() );
    }

    @Test
    void seeds_in_background()
        throws Exception
    {
        this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );
        this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 2".getBytes() ) );
        this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 3".getBytes() ) );

        new SizeBoundedBlobStore( this.delegate, 25 );

        // seeding and eviction run on background threads - wait for the bound to take effect
        for ( int i = 0; i < 200 && delegateTotalSize() > 25; i++ )
        {
            Thread.sleep( 50 );
        }
        assertTrue( delegateTotalSize() <= 25 );
    }

    @Test
    void writes_pass_through_until_seeded()
    {
        final BlobRecord existing = this.delegate.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 1".getBytes() ) );

        final List<Runnable> pendingSeed = new ArrayList<>();
        final SizeBoundedBlobStore boundedStore = new SizeBoundedBlobStore( this.delegate, 25, Runnable::run, pendingSeed::add );

        // reads are served from the delegate while seeding is pending
        assertNotNull( boundedStore.getRecord( SEGMENT, existing.getKey() ) );

        // writes go to the delegate store, but are not yet indexed - capacity is not enforced
        final BlobRecord stored = boundedStore.addRecord( SEGMENT, ByteSource.wrap( "10 bytes 2".getBytes() ) );
        assertNotNull( this.delegate.getRecord( SEGMENT, stored.getKey() ) );

        final BlobRecord source = new MemoryBlobStore().addRecord( SEGMENT, ByteSource.wrap( "10 bytes 3".getBytes() ) );
        boundedStore.addRecord( SEGMENT, source );
        assertNotNull( this.delegate.getRecord( SEGMENT, source.getKey() ) );

        assertEquals( 30, delegateTotalSize() );

        // seeding completes and indexes everything written so far - capacity is enforced again
        pendingSeed.forEach( Runnable::run );
        boundedStore.cleanUp();

        assertTrue( delegateTotalSize() <= 25 );
    }
}
