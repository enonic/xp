package com.enonic.xp.internal.blobstore.file;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreProvider;
import com.enonic.xp.blob.ProviderConfig;
import com.enonic.xp.internal.blobstore.file.config.FileBlobStoreConfig;

@Component
public final class FileBlobStoreProvider
    implements BlobStoreProvider
{
    private FileBlobStore blobStore;

    private FileBlobStoreConfig config;

    private void connect()
    {
        if ( !this.config.isValid() )
        {
            return;
        }

        this.blobStore = new FileBlobStore( config.baseDir() );
    }

    @Override
    public BlobStore get()
    {
        if ( this.blobStore == null )
        {
            connect();
        }

        return this.blobStore;
    }

    @Override
    public String name()
    {
        return "file";
    }

    @Override
    public ProviderConfig config()
    {
        return this.config;
    }

    @Reference
    public void setConfig( final FileBlobStoreConfig config )
    {
        this.config = config;
    }
}
