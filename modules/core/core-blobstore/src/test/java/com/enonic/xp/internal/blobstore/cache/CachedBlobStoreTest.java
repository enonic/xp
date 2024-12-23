package com.enonic.xp.internal.blobstore.cache;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.internal.blobstore.MemoryBlobRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class CachedBlobStoreTest
{
    private BlobStore blobStore;

    private CachedBlobStore cachedBlobStore;

    private final Segment segment = Segment.from( "test", "blob" );

    @BeforeEach
    public void setup()
    {
        this.blobStore = Mockito.mock( BlobStore.class );
        this.cachedBlobStore = CachedBlobStore.create().
            blobStore( this.blobStore ).
            memoryCapacity( 100 ).sizeThreshold( 10 ).
            build();
    }

    private BlobRecord newRecord()
    {
        return new MemoryBlobRecord( ByteSource.wrap( new byte[10] ) );
    }

    private BlobRecord newLargeRecord()
    {
        return new MemoryBlobRecord( ByteSource.wrap( new byte[20] ) );
    }

    @Test
    public void getSmallRecord()
    {
        final BlobRecord record = newRecord();
        assertNull( this.cachedBlobStore.getRecord( segment, record.getKey() ) );

        Mockito.when( this.blobStore.getRecord( segment, record.getKey() ) ).thenReturn( record );

        final BlobRecord record1 = this.cachedBlobStore.getRecord( segment, record.getKey() );
        assertNotNull( record1 );

        final BlobRecord result2 = this.cachedBlobStore.getRecord( segment, record.getKey() );
        assertNotNull( result2 );

        Mockito.verify( this.blobStore, Mockito.times( 2 ) ).getRecord( segment, record.getKey() );
    }

    @Test
    public void addSmallRecord()
    {
        final BlobRecord record = newRecord();
        final ByteSource byteSource = ByteSource.wrap( "0123".getBytes() );
        Mockito.when( this.blobStore.getRecord( segment, record.getKey() ) ).thenReturn( record );
        Mockito.when( this.blobStore.addRecord( segment, byteSource ) ).thenReturn( record );

        final BlobRecord result1 = this.cachedBlobStore.addRecord( segment, byteSource );
        assertNotNull( result1 );

        final BlobRecord result2 = this.cachedBlobStore.getRecord( segment, record.getKey() );
        assertNotNull( result2 );

        Mockito.verify( this.blobStore, Mockito.times( 0 ) ).getRecord( segment, record.getKey() );
    }

    @Test
    public void addLargeRecord()
        throws Exception
    {
        final BlobRecord record = newLargeRecord();
        final ByteSource byteSource = ByteSource.wrap( record.getBytes().read() );
        Mockito.when( this.blobStore.getRecord( segment, record.getKey() ) ).thenReturn( record );
        Mockito.when( this.blobStore.addRecord( segment, byteSource ) ).thenReturn( record );

        final BlobRecord result1 = this.cachedBlobStore.addRecord( segment, byteSource );
        assertSame( record, result1 );

        final BlobRecord result2 = this.cachedBlobStore.getRecord( segment, record.getKey() );
        assertSame( record, result2 );

        Mockito.verify( this.blobStore, Mockito.times( 1 ) ).getRecord( segment, record.getKey() );
    }

    @Test
    public void removeRecord()
        throws Exception
    {
        final BlobRecord record = newRecord();
        final ByteSource byteSource = ByteSource.wrap( "0123".getBytes() );
        Mockito.when( this.blobStore.addRecord( segment, byteSource ) ).thenReturn( record );

        this.cachedBlobStore.removeRecord( this.segment, record.getKey() );

        Mockito.verify( this.blobStore, Mockito.times( 1 ) ).removeRecord( segment, record.getKey() );

        Mockito.when( this.blobStore.getRecord( segment, record.getKey() ) ).thenReturn( null );
        assertNull( this.cachedBlobStore.getRecord( segment, record.getKey() ) );
    }

    @Test
    public void invalidate()
        throws Exception
    {
        final BlobRecord record = newRecord();
        Mockito.when( this.blobStore.getRecord( segment, record.getKey() ) ).thenReturn( record );

        this.cachedBlobStore.getRecord( this.segment, record.getKey() );
        this.cachedBlobStore.getRecord( this.segment, record.getKey() );
        this.cachedBlobStore.getRecord( this.segment, record.getKey() );
        Mockito.verify( this.blobStore, Mockito.times( 1 ) ).getRecord( segment, record.getKey() );

        this.cachedBlobStore.invalidate( this.segment, record.getKey() );

        this.cachedBlobStore.getRecord( this.segment, record.getKey() );
        Mockito.verify( this.blobStore, Mockito.times( 2 ) ).getRecord( segment, record.getKey() );
    }

    @Test
    public void lastModified()
        throws Exception
    {
        final BlobRecord record = newRecord();
        Mockito.when( this.blobStore.getRecord( segment, record.getKey() ) ).thenReturn( record );

        final BlobRecord firstRetrieval = this.cachedBlobStore.getRecord( this.segment, record.getKey() );
        assertNotNull( firstRetrieval );
        assertEquals( firstRetrieval.lastModified(), record.lastModified() );

        final BlobRecord secondRetrieval = this.cachedBlobStore.getRecord( this.segment, record.getKey() );
        assertNotNull( secondRetrieval );
        assertEquals( secondRetrieval.lastModified(), record.lastModified() );
    }

    @Test
    public void listSegments()
    {
        Mockito.when( this.blobStore.listSegments() ).thenAnswer( invocation -> Stream.of( segment ) );
        assertEquals( 1, cachedBlobStore.listSegments().count() );
        assertEquals( segment, cachedBlobStore.listSegments().findFirst().get() );
    }

    @Test
    public void deleteSegment()
    {
        final BlobRecord record = newRecord();
        assertNull( this.cachedBlobStore.getRecord( segment, record.getKey() ) );
        Mockito.when( this.blobStore.getRecord( segment, record.getKey() ) ).thenReturn( record );
        assertNotNull( cachedBlobStore.getRecord( segment, record.getKey() ) );
        Mockito.when( this.blobStore.getRecord( segment, record.getKey() ) ).thenReturn( null );
        assertNotNull( cachedBlobStore.getRecord( segment, record.getKey() ) );

        cachedBlobStore.deleteSegment( segment );
        Mockito.verify( blobStore ).deleteSegment( segment );
        assertNull( cachedBlobStore.getRecord( segment, record.getKey() ) );
    }

    @Test
    void dontCacheCorrupted() {
        final BlobRecord corruptedRecord  = new BlobRecord() {

            @Override
            public BlobKey getKey()
            {
                return BlobKey.from( "invalidKey" );
            }

            @Override
            public long getLength()
            {
                return 10;
            }

            @Override
            public ByteSource getBytes()
            {
                return ByteSource.wrap( new byte[10] );
            }

            @Override
            public long lastModified()
            {
                return 0;
            }
        };
        cachedBlobStore.addRecord( segment, corruptedRecord );
        assertNull( cachedBlobStore.getRecord( segment, BlobKey.from( "invalidKey" ) ) );
    }
}
