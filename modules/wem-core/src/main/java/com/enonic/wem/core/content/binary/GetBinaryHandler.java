package com.enonic.wem.core.content.binary;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.binary.GetBinary;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.binary.dao.BinaryDao;

@Component
public class GetBinaryHandler
    extends CommandHandler<GetBinary>
{
    private BinaryDao binaryDao;

    public GetBinaryHandler()
    {
        super( GetBinary.class );
    }

    @Override
    public void handle( final CommandContext context, final GetBinary command )
        throws Exception
    {
        final Binary result = binaryDao.getBinary( command.getBinaryId(), context.getJcrSession() );
        command.setResult( result );
    }

    @Inject
    public void setBinaryDao( final BinaryDao binaryDao )
    {
        this.binaryDao = binaryDao;
    }
}
