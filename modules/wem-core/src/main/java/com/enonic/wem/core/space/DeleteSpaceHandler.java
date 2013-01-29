package com.enonic.wem.core.space;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.space.DeleteSpace;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.SpaceDao;

@Component
public final class DeleteSpaceHandler
    extends CommandHandler<DeleteSpace>
{
    private SpaceDao spaceDao;

    public DeleteSpaceHandler()
    {
        super( DeleteSpace.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteSpace command )
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

    @Autowired
    public void setSpaceDao( final SpaceDao spaceDao )
    {
        this.spaceDao = spaceDao;
    }
}
