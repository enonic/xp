package com.enonic.xp.blobstore.file;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.BlobStoreProvider;
import com.enonic.xp.blob.ProviderConfig;
import com.enonic.xp.blobstore.file.config.FileBlobStoreConfig;

@Component
public class FileBlobStoreProvider
    implements BlobStoreProvider
{
    private FileBlobStore blobStore;

    private FileBlobStoreConfig config;

    @Activate
    public void activate()
    {
        if ( config == null )
        {
            throw new BlobStoreException( "Config [com.enonic.xp.aws.s3] not found" );
        }

        this.blobStore = new FileBlobStore( config.baseDir() );
    }

    @Override
    public BlobStore get()
    {
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

    @Override
    public boolean isActive()
    {
        return true;
    }
}
