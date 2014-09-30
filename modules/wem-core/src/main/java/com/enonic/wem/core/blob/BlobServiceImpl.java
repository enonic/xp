package com.enonic.wem.core.blob;

import java.io.InputStream;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;

public final class BlobServiceImpl
    implements BlobService
{
    private BlobStore blobStore;

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
