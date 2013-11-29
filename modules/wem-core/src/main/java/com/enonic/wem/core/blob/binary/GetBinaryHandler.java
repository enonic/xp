package com.enonic.wem.core.blob.binary;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.binary.GetBinary;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.blob.binary.dao.BlobDao;


public class GetBinaryHandler
    extends CommandHandler<GetBinary>
{
    private BlobDao blobDao;

    @Override
    public void handle()
        throws Exception
    {
        //final Binary result = binaryDao.getBinary( command.getBinaryId(), context.getJcrSession() );
        //command.setResult( result );
    }

    @Inject
    public void setBlobDao( final BlobDao blobDao )
    {
        this.blobDao = blobDao;
    }
}
