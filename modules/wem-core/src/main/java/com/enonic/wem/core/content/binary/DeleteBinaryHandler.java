package com.enonic.wem.core.content.binary;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.binary.DeleteBinary;
import com.enonic.wem.api.command.content.binary.DeleteBinaryResult;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.binary.dao.BinaryDao;


public class DeleteBinaryHandler
    extends CommandHandler<DeleteBinary>
{
    private BinaryDao binaryDao;

    @Override
    public void handle( final CommandContext context, final DeleteBinary command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        boolean deleted = binaryDao.deleteBinary( command.getBinaryId(), session );
        session.save();

        if ( deleted )
        {
            command.setResult( DeleteBinaryResult.SUCCESS );
        }
        else
        {
            command.setResult( DeleteBinaryResult.NOT_FOUND );
        }
    }

    @Inject
    public void setBinaryDao( final BinaryDao binaryDao )
    {
        this.binaryDao = binaryDao;
    }
}
