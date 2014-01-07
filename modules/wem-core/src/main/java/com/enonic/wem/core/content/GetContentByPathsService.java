package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByPaths;
import com.enonic.wem.api.command.entity.GetNodesByPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.entity.GetNodesByPathsService;


public class GetContentByPathsService
    extends ContentService
{
    private GetContentByPaths command;

    public GetContentByPathsService( final CommandContext context, final GetContentByPaths command )
    {
        super( context );
        this.command = command;
    }

    public Contents execute()
        throws Exception
    {
        final GetNodesByPaths getNodesByPathsCommand =
            new GetNodesByPaths( ContentNodeHelper.translateContentPathsToNodePaths( command.getPaths() ) );

        final Nodes nodes = new GetNodesByPathsService( session, getNodesByPathsCommand ).execute();

        return translator.fromNodes( nodes );
    }
}
