package com.enonic.wem.core.content.binary;

import javax.inject.Inject;
import javax.jcr.Session;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.content.binary.CreateBinary;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.binary.dao.BinaryDao;


public class CreateBinaryHandler
    extends CommandHandler<CreateBinary>
{
    private BinaryDao binaryDao;

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final BinaryDao.CreateBlob createBlob = new BinaryDao.CreateBlob();
        createBlob.input = ByteStreams.asByteSource( command.getBinary().toByteArray() );
        final BlobKey blobKey = binaryDao.createBinary( createBlob );
        session.save();
        command.setResult( null );
    }

    @Inject
    public void setBinaryDao( final BinaryDao binaryDao )
    {
        this.binaryDao = binaryDao;
    }
}
