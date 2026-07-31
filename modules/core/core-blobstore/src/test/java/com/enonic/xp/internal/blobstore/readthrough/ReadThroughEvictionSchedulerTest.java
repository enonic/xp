package com.enonic.xp.internal.blobstore.readthrough;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.EvictableBlobStore;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.internal.blobstore.config.BlobStoreConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReadThroughEvictionSchedulerTest
{
    @Test
    void schedules_eviction_for_evictable_store()
        throws Exception
    {
        final BlobStoreConfig config = mock( BlobStoreConfig.class );
        when( config.readThroughEvictInterval() ).thenReturn( "PT0.01S" );

        final CountDownLatch evicted = new CountDownLatch( 1 );
        final BlobStore blobStore = mock( BlobStore.class, Mockito.withSettings().extraInterfaces( EvictableBlobStore.class ) );
        when( ( (EvictableBlobStore) blobStore ).evict() ).thenAnswer( invocation -> {
            evicted.countDown();
            return 1L;
        } );

        final ReadThroughEvictionScheduler scheduler = new ReadThroughEvictionScheduler( blobStore, config );
        try
        {
            assertTrue( evicted.await( 10, TimeUnit.SECONDS ) );
        }
        finally
        {
            scheduler.deactivate();
        }
    }

    @Test
    void ignores_non_evictable_store()
    {
        final BlobStoreConfig config = mock( BlobStoreConfig.class );

        final ReadThroughEvictionScheduler scheduler = new ReadThroughEvictionScheduler( new MemoryBlobStore(), config );
        scheduler.deactivate();
    }
}
