package com.enonic.wem.core.content.space;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.space.CreateSpace;
import com.enonic.wem.api.content.space.Space;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.SpaceDao;

@Component
public final class CreateSpaceHandler
    extends CommandHandler<CreateSpace>
{
    private SpaceDao spaceDao;

    public CreateSpaceHandler()
    {
        super( CreateSpace.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateSpace command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final DateTime now = DateTime.now();
        final Space space = Space.newSpace().
            displayName( command.getDisplayName() ).
            name( command.getName() ).
            createdTime( now ).
            modifiedTime( now ).
            build();

        final Space createdSpace = spaceDao.createSpace( space, session );
        session.save();

        command.setResult( createdSpace );
    }

    @Autowired
    public void setSpaceDao( final SpaceDao spaceDao )
    {
        this.spaceDao = spaceDao;
    }
}
