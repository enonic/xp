package com.enonic.wem.core.blob.binary;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.blob.CreateBlob;
import com.enonic.wem.core.blob.binary.dao.BlobDao;
import com.enonic.wem.core.blobstore.BlobRecord;
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
        createBlob.input = command.getInputStream();
        final BlobRecord blobRecord = blobDao.create( createBlob );
        command.setResult( blobRecord );
    }

    @Inject
    public void setBlobDao( final BlobDao blobDao )
    {
        this.blobDao = blobDao;
    }
}
