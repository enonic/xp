package com.enonic.xp.internal.blobstore.file;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;

import static org.junit.Assert.*;

public class FileBlobStoreTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private FileBlobStore blobStore;

    private final Segment segment = Segment.from( "test", "blob" );

    @Before
    public void setup()
    {
        this.blobStore = new FileBlobStore( this.temporaryFolder.getRoot() );
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
        final BlobRecord record = createRecord( "hello" );

        assertNotNull( record );
        assertNotNull( record.getKey() );
        assertEquals( "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d", record.getKey().toString() );
        assertEquals( 5, record.getLength() );
        assertEquals( "hello", new String( record.getBytes().read() ) );

        this.blobStore.addRecord( this.segment, record );
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
        List<BlobRecord> stored = Lists.newArrayList();
        stored.add( createRecord( "f1" ) );
        stored.add( createRecord( "f2" ) );
        stored.add( createRecord( "f3" ) );
        stored.add( createRecord( "f4" ) );
        stored.add( createRecord( "f5" ) );
        stored.add( createRecord( "f6" ) );
        stored.add( createRecord( "f7" ) );
        final Stream<BlobRecord> stream = this.blobStore.list( this.segment );
        final List<BlobRecord> records = stream.collect( Collectors.toList() );
        assertEquals( 7, records.size() );
        assertTrue( records.containsAll( stored ) );
    }

    @Test
    public void lastModifiedUpdated()
        throws Exception
    {
        final BlobRecord rec1 = this.blobStore.addRecord( this.segment, ByteSource.wrap( "hello".getBytes() ) );
        final long beforeUpdate = rec1.lastModified();
        Thread.sleep( 1000 ); // ensure a second difference
        final BlobRecord rec2 = this.blobStore.addRecord( this.segment, ByteSource.wrap( "hello".getBytes() ) );

        assertTrue( beforeUpdate < rec2.lastModified() );
    }

    private BlobRecord createRecord( final String str )
    {
        final ByteSource source = ByteSource.wrap( str.getBytes() );
        return this.blobStore.addRecord( this.segment, source );
    }
}