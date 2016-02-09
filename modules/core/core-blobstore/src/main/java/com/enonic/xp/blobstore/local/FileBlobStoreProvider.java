package com.enonic.xp.blobstore.local;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blobstore.BlobStoreProvider;
import com.enonic.xp.blobstore.config.BlobStoreConfig;

@Component
public class FileBlobStoreProvider
    implements BlobStoreProvider
{
    private FileBlobStore blobStore;

    private BlobStoreConfig config;

    @Activate
    public void activate()
    {
        this.blobStore = new FileBlobStore( config.blobStoreDir() );
    }

    @Override
    public BlobStore get()
    {
        return this.blobStore;
    }

    @Reference
    public void setConfig( final BlobStoreConfig config )
    {
        this.config = config;
    }

    @Override
    public String name()
    {
        return "local";
    }
}
