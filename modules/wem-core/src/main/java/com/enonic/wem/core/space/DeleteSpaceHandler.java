package com.enonic.wem.core.space;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.space.DeleteSpace;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.space.dao.SpaceDao;


public final class DeleteSpaceHandler
    extends CommandHandler<DeleteSpace>
{
    private SpaceDao spaceDao;

    @Override
    public void handle( final DeleteSpace command )
        throws Exception
    {
        final Session session = context.getJcrSession();

        try
        {
            spaceDao.deleteSpace( command.getName(), session );
            session.save();
            command.setResult( true );
        }
        catch ( SpaceNotFoundException e )
        {
            command.setResult( false );
        }
    }

    @Inject
    public void setSpaceDao( final SpaceDao spaceDao )
    {
        this.spaceDao = spaceDao;
    }
}
