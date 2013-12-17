package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.entity.GetNodeByPathService;


public class GetContentByPathHandler
    extends CommandHandler<GetContentByPath>
{
    private ContentDao contentDao;

    private ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        /*
        final ContentPath contentPath = command.getPath();
        final Content result = contentDao.selectByPath( contentPath, context.getJcrSession() );

        if ( result == null )
        {
            throw new ContentNotFoundException( contentPath );
        }
        command.setResult( result );
        */

        GetNodeByPath getNodeByPathCommand = new GetNodeByPath( ContentNodeHelper.translateContentPathToNodePath( command.getPath() ) );

        final Node node = new GetNodeByPathService( this.context.getJcrSession(), getNodeByPathCommand ).execute();

        if ( node == null )
        {
            throw new ContentNotFoundException( command.getPath() );
        }

        final Content content = CONTENT_TO_NODE_TRANSLATOR.fromNode( node );

        command.setResult( content );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
