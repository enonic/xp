package com.enonic.wem.core.blob.binary;

import java.io.InputStream;

import javax.inject.Inject;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.core.blob.binary.dao.BlobDao;

public class BlobServiceImpl
    implements BlobService
{
    @Inject
    private BlobDao blobDao;

    @Override
    public Blob create( final InputStream byteSource )
    {
        final BlobDao.CreateBlob createBlob = new BlobDao.CreateBlob();
        createBlob.input = byteSource;

        return blobDao.create( createBlob );
    }

    @Override
    public Blob get( final BlobKey blobKey )
    {
        return blobDao.getBlobRecord( blobKey );
    }
}
