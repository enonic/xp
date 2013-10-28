package com.enonic.wem.core.space;

import javax.inject.Inject;
import javax.jcr.Session;

import org.joda.time.DateTime;

import com.enonic.wem.api.command.space.CreateSpace;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.space.dao.SpaceDao;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.space.Space.newSpace;


public final class CreateSpaceHandler
    extends CommandHandler<CreateSpace>
{
    private SpaceDao spaceDao;

    private ContentDao contentDao;

    private IndexService indexService;

    @Override
    public void handle()
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
            type( ContentTypeName.space() ).
            createdTime( space.getCreatedTime() ).
            modifiedTime( space.getModifiedTime() ).
            displayName( space.getDisplayName() ).
            build();
        final ContentId rootContentId = contentDao.create( rootContent, session );
        session.save();

        if ( !space.isTemporary() )
        {
            indexService.indexContent( contentDao.select( rootContentId, session ) );
        }

        final Space createdSpace = newSpace( space ).rootContent( rootContentId ).build();

        command.setResult( createdSpace );
    }

    @Inject
    public void setSpaceDao( final SpaceDao spaceDao )
    {
        this.spaceDao = spaceDao;
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
