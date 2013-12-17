package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.GetNodeByPathService;


public class GetContentByPathHandler
    extends CommandHandler<GetContentByPath>
{
    private ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final GetNodeByPath getNodeByPathCommand =
            new GetNodeByPath( ContentNodeHelper.translateContentPathToNodePath( command.getPath() ) );

        final Node node = new GetNodeByPathService( this.context.getJcrSession(), getNodeByPathCommand ).execute();

        if ( node == null )
        {
            throw new ContentNotFoundException( command.getPath() );
        }

        final Content content = CONTENT_TO_NODE_TRANSLATOR.fromNode( node );

        command.setResult( content );
    }
}
