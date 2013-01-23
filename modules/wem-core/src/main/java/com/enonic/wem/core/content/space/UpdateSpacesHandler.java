package com.enonic.wem.core.content.space;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.space.UpdateSpaces;
import com.enonic.wem.api.content.space.Space;
import com.enonic.wem.api.content.space.SpaceName;
import com.enonic.wem.api.content.space.SpaceNames;
import com.enonic.wem.api.content.space.editor.SpaceEditor;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.SpaceDao;

@Component
public final class UpdateSpacesHandler
    extends CommandHandler<UpdateSpaces>
{
    private SpaceDao spaceDao;

    public UpdateSpacesHandler()
    {
        super( UpdateSpaces.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateSpaces command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final SpaceEditor editor = command.getEditor();

        final SpaceNames spaceNames = command.getSpaceNames();
        int spacesUpdated = 0;
        for ( SpaceName spaceName : spaceNames )
        {
            final Space space = spaceDao.getSpace( spaceName, session );
            final Space modifiedSpace = editor.edit( space );
            if ( modifiedSpace != null )
            {
                spaceDao.updateSpace( modifiedSpace, session );
                spacesUpdated++;
            }
        }
        session.save();
        command.setResult( spacesUpdated );
    }

    @Autowired
    public void setSpaceDao( final SpaceDao spaceDao )
    {
        this.spaceDao = spaceDao;
    }
}
