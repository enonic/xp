package com.enonic.wem.repo.internal.blob;

import java.io.InputStream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;

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

    public ByteSource getByteSource( final BlobKey blobKey )
    {
        return this.blobStore.getByteSource( blobKey );
    }

    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }
}
