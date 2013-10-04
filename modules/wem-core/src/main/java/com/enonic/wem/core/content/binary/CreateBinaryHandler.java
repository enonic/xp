package com.enonic.wem.core.content.binary;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.binary.CreateBinary;
import com.enonic.wem.api.content.binary.BinaryId;
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
        final BinaryId binaryId = binaryDao.createBinary( command.getBinary(), session );
        session.save();
        command.setResult( binaryId );
    }

    @Inject
    public void setBinaryDao( final BinaryDao binaryDao )
    {
        this.binaryDao = binaryDao;
    }
}
