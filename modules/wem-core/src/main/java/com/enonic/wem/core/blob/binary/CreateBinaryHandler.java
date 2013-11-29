package com.enonic.wem.core.blob.binary;

import javax.inject.Inject;
import javax.jcr.Session;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.content.binary.CreateBinary;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.blob.binary.dao.BlobDao;


public class CreateBinaryHandler
    extends CommandHandler<CreateBinary>
{
    private BlobDao blobDao;

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final BlobDao.CreateBlob createBlob = new BlobDao.CreateBlob();
        createBlob.input = ByteStreams.asByteSource( command.getBinary().toByteArray() );
        final BlobKey blobKey = blobDao.createBinary( createBlob );
        session.save();
        command.setResult( null );
    }

    @Inject
    public void setBlobDao( final BlobDao blobDao )
    {
        this.blobDao = blobDao;
    }
}
