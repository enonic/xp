package com.enonic.wem.internal.blob;

import java.io.InputStream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.internal.blob.file.FileBlobStore;

@Component
public final class BlobServiceImpl
    implements BlobService
{
    private BlobStore blobStore;

    @Activate
    public void activate()
    {
        if ( this.blobStore == null )
        {
            this.blobStore = new FileBlobStore();
        }
    }

    @Override
    public Blob create( final InputStream in )
    {
        return blobStore.addRecord( in );
    }

    @Override
    public Blob get( final BlobKey blobKey )
    {
        return this.blobStore.getRecord( blobKey );
    }

    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }
}
