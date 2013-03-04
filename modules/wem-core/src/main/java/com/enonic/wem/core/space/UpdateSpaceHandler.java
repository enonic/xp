package com.enonic.wem.core.space;

import javax.inject.Inject;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.space.UpdateSpace;
import com.enonic.wem.api.exception.SpaceNotFoundException;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.editor.SpaceEditor;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.space.dao.SpaceDao;

@Component
public final class UpdateSpaceHandler
    extends CommandHandler<UpdateSpace>
{
    private SpaceDao spaceDao;

    public UpdateSpaceHandler()
    {
        super( UpdateSpace.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateSpace command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final SpaceEditor editor = command.getEditor();
        final SpaceName spaceName = command.getSpaceName();

        final Space space = spaceDao.getSpace( spaceName, session );
        if ( space == null )
        {
            throw new SpaceNotFoundException( spaceName );
        }
        final Space editedSpace = editor.edit( space );

        if ( editedSpace != null )
        {
            spaceDao.updateSpace( editedSpace, session );
        }
        boolean spaceModified = editedSpace != null;

        session.save();
        command.setResult( spaceModified );
    }

    @Inject
    public void setSpaceDao( final SpaceDao spaceDao )
    {
        this.spaceDao = spaceDao;
    }
}
