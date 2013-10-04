package com.enonic.wem.core.space;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.space.RenameSpace;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.space.dao.SpaceDao;


public final class RenameSpaceHandler
    extends CommandHandler<RenameSpace>
{
    private SpaceDao spaceDao;

    @Override
    public void handle( final RenameSpace command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final SpaceName spaceName = command.getSpace();
        final String newName = command.getNewName();

        final Space space = spaceDao.getSpace( spaceName, session );
        if ( space == null )
        {
            throw new SpaceNotFoundException( spaceName );
        }
        else
        {
            final boolean spaceRenamed = spaceDao.renameSpace( spaceName, newName, session );
            if ( spaceRenamed )
            {
                session.save();
            }
            command.setResult( spaceRenamed );
        }
    }

    @Inject
    public void setSpaceDao( final SpaceDao spaceDao )
    {
        this.spaceDao = spaceDao;
    }
}
