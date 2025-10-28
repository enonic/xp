package com.enonic.xp.internal.blobstore;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.ProviderConfig;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.internal.blobstore.config.BlobStoreConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BlobStoreFactoryTest
{
    @Test
    void no_cache_no_provider()
    {
        final MemoryBlobStore blobStore = new MemoryBlobStore();

        final MemoryBlobStoreProvider memoryBlobStoreProvider =
            new MemoryBlobStoreProvider( "memory", blobStore, createProviderConfig( "none", false ) );

        final BlobStore finalBlobStore = BlobStoreFactory.create().
            provider( memoryBlobStoreProvider ).
            config( createBlobstoreConfig( false ) ).
            build().
            execute();

        assertNotNull( finalBlobStore );

        final Segment segment = Segment.from( "test", "blob" );
        final BlobRecord record = finalBlobStore.addRecord( segment, ByteSource.wrap( "hei".getBytes() ) );

        assertEquals( finalBlobStore.getRecord( segment, record.getKey() ), blobStore.getRecord( segment, record.getKey() ) );
    }

    @Test
    void with_provider()
    {
        doTestProviderStore( false );
    }

    @Test
    void provider_and_cache()
    {
        doTestProviderStore( true );
    }

    private void doTestProviderStore( final boolean cache )
    {
        final MemoryBlobStore memory1 = new MemoryBlobStore();
        final MemoryBlobStoreProvider provider1 =
            new MemoryBlobStoreProvider( "memory1", memory1, createProviderConfig( "memory2", true ) );

        final MemoryBlobStore memory2 = new MemoryBlobStore();
        final MemoryBlobStoreProvider provider2 = new MemoryBlobStoreProvider( "memory2", memory2, createProviderConfig( null, false ) );

        final BlobStoreProviders blobStoreProviders = new BlobStoreProviders();
        blobStoreProviders.add( provider2 );

        final BlobStore finalBlobStore = BlobStoreFactory.create().
            provider( provider1 ).
            config( createBlobstoreConfig( cache ) ).
            providers( blobStoreProviders ).
            build().
            execute();

        assertNotNull( finalBlobStore );

        final Segment segment = Segment.from( "test", "blob" );
        final BlobRecord record = finalBlobStore.addRecord( segment, ByteSource.wrap( "hei".getBytes() ) );

        assertEquals( finalBlobStore.getRecord( segment, record.getKey() ), memory1.getRecord( segment, record.getKey() ) );
        assertEquals( finalBlobStore.getRecord( segment, record.getKey() ), memory2.getRecord( segment, record.getKey() ) );
    }


    private ProviderConfig createProviderConfig( final String readThroughProviderName, final boolean readThroughEnabled )
    {
        return new ProviderConfig()
        {
            @Override
            public String readThroughProvider()
            {
                return readThroughProviderName;
            }

            @Override
            public boolean readThroughEnabled()
            {
                return readThroughEnabled;
            }

            @Override
            public long readThroughSizeThreshold()
            {
                return 0;
            }

            @Override
            public boolean isValid()
            {
                return true;
            }
        };
    }


    private BlobStoreConfig createBlobstoreConfig( final boolean cache )
    {
        return new BlobStoreConfig()
        {
            @Override
            public String providerName()
            {
                return "memory";
            }

            @Override
            public boolean cache()
            {
                return cache;
            }

            @Override
            public long cacheSizeThreshold()
            {
                return 0;
            }

            @Override
            public long memoryCapacity()
            {
                return 0;
            }
        };
    }
}
