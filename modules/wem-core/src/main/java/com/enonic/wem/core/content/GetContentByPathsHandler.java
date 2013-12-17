package com.enonic.wem.core.content;

import java.util.Iterator;

import com.enonic.wem.api.command.content.GetContentByPaths;
import com.enonic.wem.api.command.entity.GetNodesByPaths;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.GetNodesByPathsService;


public class GetContentByPathsHandler
    extends CommandHandler<GetContentByPaths>
{
    private ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final GetNodesByPaths getNodesByPathsCommand = new GetNodesByPaths( fromContentPaths( command.getPaths() ) );

        final Nodes nodes = new GetNodesByPathsService( this.context.getJcrSession(), getNodesByPathsCommand ).execute();

        command.setResult( CONTENT_TO_NODE_TRANSLATOR.fromNodes( nodes ) );
    }

    private NodePaths fromContentPaths( final ContentPaths contentPaths )
    {
        final NodePaths.Builder builder = NodePaths.newNodePaths();

        final Iterator<ContentPath> iterator = contentPaths.iterator();
        while ( iterator.hasNext() )
        {
            builder.addNodePath( ContentNodeHelper.translateContentPathToNodePath( iterator.next() ) );
        }

        return builder.build();
    }
}
