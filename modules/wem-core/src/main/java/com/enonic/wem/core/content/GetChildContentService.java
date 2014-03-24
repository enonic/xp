package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.GetNodesByParentParams;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandContext;


public class GetChildContentService
    extends ContentService
{
    private final GetChildContent command;

    private boolean populateChildIds = false;

    public GetChildContentService( final CommandContext context, final GetChildContent command, final NodeService nodeService )
    {
        super( context, nodeService );
        this.command = command;
    }

    GetChildContentService populateChildIds( boolean value )
    {
        this.populateChildIds = value;
        return this;
    }

    public Contents execute()
        throws Exception
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( command.getParentPath() );

        final Nodes nodes = nodeService.getByParent( new GetNodesByParentParams( nodePath ) );
        final Contents contents = translator.fromNodes( removeNonContentNodes( nodes ) );

        if ( populateChildIds )
        {
            return new ChildContentIdsResolver( context, this.nodeService ).resolve( contents );
        }
        else
        {
            return contents;
        }
    }

}
