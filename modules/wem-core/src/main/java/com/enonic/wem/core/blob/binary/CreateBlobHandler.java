package com.enonic.wem.core.blob.binary;

import javax.inject.Inject;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.content.binary.CreateBlob;
import com.enonic.wem.core.blob.binary.dao.BlobDao;
import com.enonic.wem.core.command.CommandHandler;


public class CreateBlobHandler
    extends CommandHandler<CreateBlob>
{
    private BlobDao blobDao;

    @Override
    public void handle()
        throws Exception
    {
        final BlobDao.CreateBlob createBlob = new BlobDao.CreateBlob();
        createBlob.input = command.getByteSource();
        final BlobKey blobKey = blobDao.create( createBlob );
        command.setResult( blobKey );
    }

    @Inject
    public void setBlobDao( final BlobDao blobDao )
    {
        this.blobDao = blobDao;
    }
}
