package com.enonic.xp.blobstore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.enonic.xp.blobstore.config.BlobStoreConfig;
import com.enonic.xp.blobstore.file.FileBlobStoreProvider;
import com.enonic.xp.blobstore.file.config.FileBlobStoreConfig;

public class BlobStoreActivatorTest
{

    @Rule
    public TemporaryFolder baseDir = new TemporaryFolder();

    @Test
    public void testName()
        throws Exception
    {
        final BlobStoreConfig blobStoreConfig = Mockito.mock( BlobStoreConfig.class );
        Mockito.when( blobStoreConfig.providerName() ).
            thenReturn( "file" );

        FileBlobStoreConfig fileBlobStoreConfig = Mockito.mock( FileBlobStoreConfig.class );
        Mockito.when( fileBlobStoreConfig.baseDir() ).thenReturn( this.baseDir.getRoot() );
        final FileBlobStoreProvider fileBlobStore = new FileBlobStoreProvider();
        fileBlobStore.setConfig( fileBlobStoreConfig );
        fileBlobStore.get();

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final BlobStoreActivator blobStoreActivator = new BlobStoreActivator();
        blobStoreActivator.addProvider( fileBlobStore );
        blobStoreActivator.setConfig( blobStoreConfig );
        blobStoreActivator.activate( bundleContext );

        final BundleContext context = Mockito.mock( BundleContext.class );

        blobStoreActivator.activate( context );


    }
}