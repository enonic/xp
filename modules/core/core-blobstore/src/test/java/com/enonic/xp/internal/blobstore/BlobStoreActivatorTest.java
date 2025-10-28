package com.enonic.xp.internal.blobstore;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.enonic.xp.blob.ProviderConfig;
import com.enonic.xp.internal.blobstore.config.BlobStoreConfig;

class BlobStoreActivatorTest
{
    @Test
    void activate()
        throws Exception
    {
        final BlobStoreConfig blobStoreConfig = Mockito.mock( BlobStoreConfig.class );
        Mockito.when( blobStoreConfig.providerName() ).
            thenReturn( "file" );

        final MemoryBlobStore blobStore = new MemoryBlobStore();
        final MemoryBlobStoreProvider provider = new MemoryBlobStoreProvider( "memoey", blobStore, new ProviderConfig()
        {
            @Override
            public String readThroughProvider()
            {
                return null;
            }

            @Override
            public boolean readThroughEnabled()
            {
                return false;
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
        } );

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final BlobStoreActivator blobStoreActivator = new BlobStoreActivator();
        blobStoreActivator.addProvider( provider );
        blobStoreActivator.setConfig( blobStoreConfig );
        blobStoreActivator.activate( bundleContext );

        final BundleContext context = Mockito.mock( BundleContext.class );

        blobStoreActivator.activate( context );
    }
}
