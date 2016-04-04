package com.enonic.xp.blobstore;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.enonic.xp.blobstore.config.BlobStoreConfig;

public class BlobStoreActivatorTest
{
    @Test
    public void activate()
        throws Exception
    {
        final BlobStoreConfig blobStoreConfig = Mockito.mock( BlobStoreConfig.class );
        Mockito.when( blobStoreConfig.providerName() ).
            thenReturn( "file" );

        final MemoryBlobStore blobStore = new MemoryBlobStore();
        final MemoryBlobStoreProvider provider = new MemoryBlobStoreProvider( blobStore );

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final BlobStoreActivator blobStoreActivator = new BlobStoreActivator();
        blobStoreActivator.addProvider( provider );
        blobStoreActivator.setConfig( blobStoreConfig );
        blobStoreActivator.activate( bundleContext );

        final BundleContext context = Mockito.mock( BundleContext.class );

        blobStoreActivator.activate( context );
    }
}