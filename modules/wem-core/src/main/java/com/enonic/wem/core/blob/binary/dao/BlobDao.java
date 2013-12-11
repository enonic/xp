package com.enonic.wem.core.blob.binary.dao;


import java.io.InputStream;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.core.blobstore.BlobRecord;
import com.enonic.wem.core.blobstore.BlobStoreException;

public interface BlobDao
{
    BlobRecord create( CreateBlob createBlob )
        throws BlobStoreException;

    BlobRecord getBlobRecord( BlobKey blobKey );

    public class CreateBlob
    {
        public InputStream input;
    }

}
