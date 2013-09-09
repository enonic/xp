package com.enonic.wem.core.content;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.Spaces;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.space.dao.SpaceDao;

public class GetRootContentHandler
    extends CommandHandler<GetRootContent>
{

    private SpaceDao spaceDao;

    private ContentDao contentDao;

    public GetRootContentHandler()
    {
        super( GetRootContent.class );
    }

    @Override
    public void handle( final CommandContext context, final GetRootContent command )
        throws Exception
    {

        final Session jcrSession = context.getJcrSession();
        final Spaces spaces = spaceDao.getAllSpaces( jcrSession );
        final List<Content> contents = new ArrayList<>();
        for ( Space space : spaces )
        {
            Content content = contentDao.select( ContentPath.rootOf( space.getName() ), jcrSession );
            if ( null != content )
            {
                contents.add( content );
            }
        }
        command.setResult( Contents.from( contents ) );
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
}
