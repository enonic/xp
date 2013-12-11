package com.enonic.wem.core.blob.binary;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.blob.GetBlob;
import com.enonic.wem.core.blob.binary.dao.BlobDao;
import com.enonic.wem.core.blobstore.BlobRecord;
import com.enonic.wem.core.command.CommandHandler;


public class GetBlobHandler
    extends CommandHandler<GetBlob>
{
    private BlobDao blobDao;

    @Override
    public void handle()
        throws Exception
    {
        final BlobRecord blobRecord = blobDao.getBlobRecord( command.getBlobKey() );
        command.setResult( blobRecord );
    }

    @Inject
    public void setBlobDao( final BlobDao blobDao )
    {
        this.blobDao = blobDao;
    }
}
