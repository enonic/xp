package com.enonic.wem.core.space;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.space.CreateSpace;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.space.dao.SpaceDao;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.space.Space.newSpace;

@Component
public final class CreateSpaceHandler
    extends CommandHandler<CreateSpace>
{
    private SpaceDao spaceDao;

    private ContentDao contentDao;

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
        final Space space = newSpace().
            displayName( command.getDisplayName() ).
            name( command.getName() ).
            createdTime( now ).
            modifiedTime( now ).
            icon( command.getIcon() ).
            build();
        spaceDao.createSpace( space, session );

        final Content rootContent = newContent().
            path( ContentPath.rootOf( space.getName() ) ).
            type( QualifiedContentTypeName.space() ).
            createdTime( space.getCreatedTime() ).
            modifiedTime( space.getModifiedTime() ).
            displayName( space.getDisplayName() ).
            build();
        final ContentId rootContentId = contentDao.create( rootContent, session );
        session.save();

        final Space createdSpace = newSpace( space ).rootContent( rootContentId ).build();

        command.setResult( createdSpace );
    }

    @Autowired
    public void setSpaceDao( final SpaceDao spaceDao )
    {
        this.spaceDao = spaceDao;
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
