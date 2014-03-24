package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.GetNodesByPathsParams;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandContext;


public class GetContentByPathsService
    extends ContentService
{
    private GetContentByPaths command;

    public GetContentByPathsService( final CommandContext context, final GetContentByPaths command, final NodeService nodeService )
    {
        super( context, nodeService );
        this.command = command;
    }

    public Contents execute()
        throws Exception
    {
        final NodePaths paths = ContentNodeHelper.translateContentPathsToNodePaths( command.getPaths() );
        final Nodes nodes = nodeService.getByPaths( new GetNodesByPathsParams( paths ) );

        return translator.fromNodes( nodes );
    }
}
