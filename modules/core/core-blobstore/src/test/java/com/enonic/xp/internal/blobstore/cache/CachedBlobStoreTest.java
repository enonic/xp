package com.enonic.xp.internal.blobstore.cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;

import static org.junit.Assert.*;

public class CachedBlobStoreTest
{
    private BlobStore blobStore;

    private CachedBlobStore cachedBlobStore;

    private final Segment segment = Segment.from( "test" );

    @Before
    public void setup()
    {
        this.blobStore = Mockito.mock( BlobStore.class );
        this.cachedBlobStore = CachedBlobStore.create().
            blobStore( this.blobStore ).
            memoryCapacity( 100 ).
            sizeTreshold( 10 ).
            build();
    }

    private BlobRecord newRecord( final String key, final long size )
    {
        final BlobRecord record = Mockito.mock( BlobRecord.class );
        Mockito.when( record.getKey() ).thenReturn( BlobKey.from( key ) );
        Mockito.when( record.getLength() ).thenReturn( size );
        Mockito.when( record.getBytes() ).thenReturn( ByteSource.wrap( "these are my bytes".getBytes() ) );
        return record;
    }

    @Test
    public void getSmallRecord()
    {
        final BlobRecord record = newRecord( "0123", 10L );
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
        final BlobRecord record = newRecord( "0123", 10L );
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
    {
        final BlobRecord record = newRecord( "0123", 20L );
        final ByteSource byteSource = ByteSource.wrap( "0123".getBytes() );
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
        final BlobRecord record = newRecord( "0123", 10L );
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
        final BlobRecord record = newRecord( "0123", 10L );
        Mockito.when( this.blobStore.getRecord( segment, record.getKey() ) ).thenReturn( record );

        this.cachedBlobStore.getRecord( this.segment, record.getKey() );
        this.cachedBlobStore.getRecord( this.segment, record.getKey() );
        this.cachedBlobStore.getRecord( this.segment, record.getKey() );
        Mockito.verify( this.blobStore, Mockito.times( 1 ) ).getRecord( segment, record.getKey() );

        this.cachedBlobStore.invalidate( this.segment, record.getKey() );

        this.cachedBlobStore.getRecord( this.segment, record.getKey() );
        Mockito.verify( this.blobStore, Mockito.times( 2 ) ).getRecord( segment, record.getKey() );
    }

}
