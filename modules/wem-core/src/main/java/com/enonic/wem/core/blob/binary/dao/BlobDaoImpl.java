package com.enonic.wem.core.blob.binary.dao;


import javax.inject.Inject;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.core.blobstore.BlobRecord;
import com.enonic.wem.core.blobstore.BlobStore;
import com.enonic.wem.core.blobstore.BlobStoreException;


public class BlobDaoImpl
    implements BlobDao
{

    private BlobStore blobStore;

    @Override
    public BlobRecord create( final CreateBlob createBlob )
        throws BlobStoreException
    {
        return blobStore.addRecord( createBlob.input );
    }

    @Override
    public BlobRecord getBlobRecord( final BlobKey blobKey )
    {
        return this.blobStore.getRecord( blobKey );
    }

    @Inject
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }
}
