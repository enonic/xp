package com.enonic.xp.internal.blobstore.readthrough;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.util.ByteSizeParser;

import static org.junit.Assert.*;

public class ReadThroughBlobStoreTest
{
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private MemoryBlobStore readThroughStore;

    private MemoryBlobStore finalStore;

    @Before
    public void setUp()
        throws Exception
    {
        this.readThroughStore = new MemoryBlobStore();
        this.finalStore = new MemoryBlobStore();
    }

    @Test
    public void stored_in_readthrough()
        throws Exception
    {
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            sizeThreshold( ByteSizeParser.parse( "80kb" ) ).
            build();

        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );

        final Segment segment = Segment.from( "test", "blob" );
        final BlobRecord record = actualBlobStore.addRecord( segment, binary );

        assertNotNull( this.readThroughStore.getRecord( segment, record.getKey() ) );
        assertNotNull( this.finalStore.getRecord( segment, record.getKey() ) );
    }

    @Test
    public void stored_after_read()
        throws Exception
    {
        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );
        final Segment segment = Segment.from( "test", "blob" );

        final BlobRecord record = this.finalStore.addRecord( segment, binary );

        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            build();

        assertNull( this.readThroughStore.getRecord( segment, record.getKey() ) );

        actualBlobStore.getRecord( segment, record.getKey() );

        assertNotNull( this.readThroughStore.getRecord( segment, record.getKey() ) );
    }

    @Test
    public void obey_size_threshold()
        throws Exception
    {
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            sizeThreshold( ByteSizeParser.parse( "80kb" ) ).
            build();

        final ByteSource binary = ByteSource.wrap( ByteStreams.toByteArray( this.getClass().getResourceAsStream( "fish-86kb.jpg" ) ) );

        final Segment segment = Segment.from( "test", "blob" );
        final BlobRecord record = actualBlobStore.addRecord( segment, binary );

        assertNull( this.readThroughStore.getRecord( segment, record.getKey() ) );
        assertNotNull( this.finalStore.getRecord( segment, record.getKey() ) );
    }

    @Test
    public void removeRecord()
        throws Exception
    {
        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );
        final Segment segment = Segment.from( "test", "blob" );

        final BlobRecord record = this.finalStore.addRecord( segment, binary );
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            build();

        assertNull( this.readThroughStore.getRecord( segment, record.getKey() ) );
        actualBlobStore.getRecord( segment, record.getKey() );
        assertNotNull( this.readThroughStore.getRecord( segment, record.getKey() ) );

        actualBlobStore.removeRecord( segment, record.getKey() );
        assertNull( this.readThroughStore.getRecord( segment, record.getKey() ) );
        assertNull( this.finalStore.getRecord( segment, record.getKey() ) );
    }

    @Test
    public void list()
        throws Exception
    {
        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );
        final Segment segment = Segment.from( "test", "blob" );

        final BlobRecord record = this.finalStore.addRecord( segment, binary );
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            build();

        final Stream<BlobRecord> stream = actualBlobStore.list( segment );
        final List<BlobRecord> records = stream.collect( Collectors.toList() );

        assertTrue( records.contains( record ) );
    }

    @Test
    public void invalidate()
        throws Exception
    {
        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );
        final Segment segment = Segment.from( "test", "blob" );

        final BlobRecord record = this.finalStore.addRecord( segment, binary );
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            build();

        assertNull( this.readThroughStore.getRecord( segment, record.getKey() ) );
        actualBlobStore.getRecord( segment, record.getKey() );
        assertNotNull( this.readThroughStore.getRecord( segment, record.getKey() ) );

        actualBlobStore.invalidate( segment, record.getKey() );
        assertNull( this.readThroughStore.getRecord( segment, record.getKey() ) );
        assertNotNull( this.finalStore.getRecord( segment, record.getKey() ) );
    }


    @Test
    public void last_modified()
        throws Exception
    {
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            sizeThreshold( ByteSizeParser.parse( "80kb" ) ).
            build();

        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );

        final Segment segment = Segment.from( "test", "blob" );
        final BlobRecord record = actualBlobStore.addRecord( segment, binary );

        final BlobRecord readThroughRecord = this.readThroughStore.getRecord( segment, record.getKey() );
        assertNotNull( readThroughRecord );
        final BlobRecord fileRecord = this.finalStore.getRecord( segment, record.getKey() );
        assertNotNull( fileRecord );

        assertEquals( readThroughRecord.lastModified(), fileRecord.lastModified() );
    }

    @Test
    public void last_modified_updated()
        throws Exception
    {
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            sizeThreshold( ByteSizeParser.parse( "80kb" ) ).
            build();

        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );

        final Segment segment = Segment.from( "test", "blob" );
        final BlobRecord record = actualBlobStore.addRecord( segment, binary );

        final BlobRecord readThroughRecord = this.readThroughStore.getRecord( segment, record.getKey() );
        final BlobRecord fileRecord = this.finalStore.getRecord( segment, record.getKey() );
        assertEquals( readThroughRecord.lastModified(), fileRecord.lastModified() );

        Thread.sleep( 1000 ); // ensure a second difference since

        actualBlobStore.addRecord( segment, binary );
        final BlobRecord readThroughUpdated = this.readThroughStore.getRecord( segment, record.getKey() );
        final BlobRecord finalUpdated = this.finalStore.getRecord( segment, record.getKey() );
        assertEquals( readThroughUpdated.lastModified(), finalUpdated.lastModified() );
        assertTrue( readThroughUpdated.lastModified() > readThroughRecord.lastModified() );
    }

    @Test
    public void listSegments()
    {
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( readThroughStore ).
            store( finalStore ).
            build();

        assertEquals( 0, actualBlobStore.listSegments().count() );

        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );
        final Segment segment = Segment.from( "test", "blob" );
        finalStore.addRecord( segment, binary );
        assertEquals( 1, actualBlobStore.listSegments().count() );
    }

    @Test
    public void deleteSegment()
    {
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( readThroughStore ).
            store( finalStore ).
            build();
        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );
        final Segment segment = Segment.from( "test", "blob" );
        final BlobRecord record = finalStore.addRecord( segment, binary );
        assertEquals( 1, actualBlobStore.listSegments().count() );
        assertEquals( record, actualBlobStore.getRecord( segment, record.getKey() ) );

        finalStore.removeRecord( segment, record.getKey() );
        assertEquals( record, actualBlobStore.getRecord( segment, record.getKey() ) );

        actualBlobStore.deleteSegment( segment );
        assertEquals( 0, actualBlobStore.listSegments().count() );
        assertNull( actualBlobStore.getRecord( segment, record.getKey() ) );

    }

}