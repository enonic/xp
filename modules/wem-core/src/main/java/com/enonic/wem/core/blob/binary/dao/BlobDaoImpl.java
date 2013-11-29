package com.enonic.wem.core.blob.binary.dao;


import java.io.IOException;

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
    public BlobKey createBinary( final CreateBlob createBlob )
        throws BlobStoreException
    {
        try
        {
            final BlobRecord blobRecord = blobStore.addRecord( createBlob.input.openStream() );
            return blobRecord.getKey();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to createBinary", e );
        }
    }

    @Override
    public BlobRecord getBinary( final BlobKey blobKey )
    {
        return this.blobStore.getRecord( blobKey );
    }

    @Inject
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }
}
