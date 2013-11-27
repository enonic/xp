package com.enonic.wem.core.content.binary.dao;


import com.google.common.io.ByteSource;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.core.blobstore.BlobRecord;
import com.enonic.wem.core.blobstore.BlobStoreException;

public interface BinaryDao
{
    BlobKey createBinary( CreateBlob createBlob )
        throws BlobStoreException;

    BlobRecord getBinary( BlobKey blobKey );

    public class CreateBlob
    {
        public ByteSource input;
    }

}
