package com.enonic.xp.internal.blobstore.file;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FileBlobStoreTest
{
    @TempDir
    public Path temporaryFolder;


    private FileBlobStore blobStore;

    private final Segment segment = Segment.from( "test", "blob" );

    @BeforeEach
    public void setup()
    {
        this.blobStore = new FileBlobStore( this.temporaryFolder );
    }

    @Test
    public void getRecord()
    {
        final BlobKey key = createRecord( "hello" ).getKey();
        final BlobRecord record = this.blobStore.getRecord( this.segment, key );
        assertNotNull( record );
    }

    @Test
    public void getRecord_notFound()
    {
        final BlobRecord record = this.blobStore.getRecord( this.segment, BlobKey.from( "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d" ) );
        assertNull( record );
    }

    @Test
    public void addRecord()
        throws Exception
    {
        final BlobRecord record = this.blobStore.addRecord( this.segment, ByteSource.wrap( "hello".getBytes() ) );
        assertNotNull( record );
        assertNotNull( record.getKey() );
        assertEquals( "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d", record.getKey().toString() );
        assertEquals( 5, record.getLength() );
        assertEquals( "hello", new String( record.getBytes().read() ) );
    }

    @Test
    public void removeRecord()
        throws Exception
    {
        final BlobRecord createdRecord = createRecord( "bye" );
        final BlobRecord retrievedRecord = this.blobStore.getRecord( this.segment, createdRecord.getKey() );
        assertNotNull( retrievedRecord );
        assertEquals( createdRecord.getKey(), retrievedRecord.getKey() );

        this.blobStore.removeRecord( this.segment, createdRecord.getKey() );
        final BlobRecord removedRecord = this.blobStore.getRecord( this.segment, createdRecord.getKey() );
        assertNull( removedRecord );
    }

    @Test
    public void list()
        throws Exception
    {
        List<BlobRecord> stored = new ArrayList<>();
        stored.add( createRecord( "f1" ) );
        stored.add( createRecord( "f2" ) );
        stored.add( createRecord( "f3" ) );
        stored.add( createRecord( "f4" ) );
        stored.add( createRecord( "f5" ) );
        stored.add( createRecord( "f6" ) );
        stored.add( createRecord( "f7" ) );
        try (Stream<BlobRecord> stream = this.blobStore.list( this.segment )) {
            assertThat(stream).containsAll( stored );
        }
    }

    @Test
    public void listSegments()
    {
        final Segment secondSegment = Segment.from( "test", "blob2" );
        assertEquals( 0, blobStore.listSegments().count() );

        createRecord( "hello" ).getKey();
        assertEquals( 1, blobStore.listSegments().count() );
        assertEquals( segment, blobStore.listSegments().findFirst().get() );

        createRecord( secondSegment, "hello" ).getKey();
        assertEquals( 2, blobStore.listSegments().count() );
    }

    @Test
    public void deleteSegment()
    {
        final Segment secondSegment = Segment.from( "test", "blob2" );
        createRecord( "hello" ).getKey();
        createRecord( secondSegment, "hello" ).getKey();
        blobStore.deleteSegment( secondSegment );
        assertEquals( 1, blobStore.listSegments().count() );
        assertEquals( segment, blobStore.listSegments().findFirst().get() );
    }

    private BlobRecord createRecord( final String str )
    {
        return createRecord( segment, str );
    }

    private BlobRecord createRecord( final Segment segment, final String str )
    {
        final ByteSource source = ByteSource.wrap( str.getBytes() );
        return this.blobStore.addRecord( segment, source );
    }
}
