package com.enonic.xp.blobstore.file;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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

    private final Segment segment = Segment.from( "test" );

    @Before
    public void setup()
    {
        this.blobStore = new FileBlobStore( this.temporaryFolder.getRoot() );
    }

    @Test
    public void getRecord()
    {
        final BlobKey key = createRecord( "hello" ).getKey();
        final BlobRecord record = this.blobStore.getRecord( segment, key );
        assertNotNull( record );
    }

    @Test
    public void getRecord_notFound()
    {
        final BlobRecord record = this.blobStore.getRecord( segment, new BlobKey( "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d" ) );
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
    }

    private BlobRecord createRecord( final String str )
    {
        final ByteSource source = ByteSource.wrap( str.getBytes() );
        return this.blobStore.addRecord( segment, source );
    }
}
