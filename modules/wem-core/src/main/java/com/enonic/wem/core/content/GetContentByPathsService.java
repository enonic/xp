package com.enonic.wem.core.content;

import javax.jcr.Session;

import com.enonic.wem.api.command.content.GetContentByPaths;
import com.enonic.wem.api.command.entity.GetNodesByPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.GetNodesByPathsService;


class GetContentByPathsService
extends ContentService
{
    private GetContentByPaths command;

    GetContentByPathsService( final Session session, final GetContentByPaths command )
    {
        super( session );
        this.command = command;
    }

    Contents execute()
    throws Exception
    {
        final GetNodesByPaths getNodesByPathsCommand =
            new GetNodesByPaths( ContentNodeHelper.translateContentPathsToNodePaths( command.getPaths() ) );

        final Nodes nodes = new GetNodesByPathsService( session, getNodesByPathsCommand ).execute();

        return CONTENT_TO_NODE_TRANSLATOR.fromNodes( nodes );
    }
}
