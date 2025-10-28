package com.enonic.xp.internal.blobstore.readthrough;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.util.ByteSizeParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadThroughBlobStoreTest
{
    @TempDir
    public Path temporaryFolder;

    private MemoryBlobStore readThroughStore;

    private MemoryBlobStore finalStore;

    @BeforeEach
    void setUp()
    {
        this.readThroughStore = new MemoryBlobStore();
        this.finalStore = new MemoryBlobStore();
    }

    @Test
    void stored_in_readthrough()
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
    void stored_after_read()
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
    void obey_size_threshold()
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
    void removeRecord()
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
    void list()
    {
        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );
        final Segment segment = Segment.from( "test", "blob" );

        final BlobRecord record = this.finalStore.addRecord( segment, binary );
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            build();

        try (Stream<BlobRecord> stream = actualBlobStore.list( segment ))
        {
            assertThat( stream ).contains( record );
        }
    }

    @Test
    void invalidate()
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
    void last_modified()
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
    void last_modified_updated()
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
    void listSegments()
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
    void deleteSegment()
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
