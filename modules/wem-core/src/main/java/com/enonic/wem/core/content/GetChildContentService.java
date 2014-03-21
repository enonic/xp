package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.entity.GetNodesByParentService;


public class GetChildContentService
    extends ContentService
{
    private final GetChildContent command;

    private boolean populateChildIds = false;

    public GetChildContentService( final CommandContext context, final GetChildContent command )
    {
        super( context );
        this.command = command;
    }

    GetChildContentService populateChildIds( boolean value )
    {
        this.populateChildIds = value;
        return this;
    }

    public Contents execute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( command.getParentPath() );
        final Nodes nodes = new GetNodesByParentService( session, nodePath ).execute();
        final Contents contents = translator.fromNodes( removeNonContentNodes( nodes ) );

        if ( populateChildIds )
        {
            return new ChildContentIdsResolver( context ).resolve( contents );
        }
        else
        {
            return contents;
        }
    }

}
