package com.enonic.wem.core.content;

import javax.jcr.Session;

import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.GetNodesByParentService;


public class GetChildContentService
    extends ContentService
{
    private final static ContentHasChildPopulator CONTENT_HAS_CHILD_POPULATOR = new ContentHasChildPopulator();

    private final GetChildContent command;

    public GetChildContentService( final Session session, final GetChildContent command )
    {
        super( session );
        this.command = command;
    }

    public Contents execute()
    {
        final GetNodesByParent getNodesByParentCommand =
            new GetNodesByParent( ContentNodeHelper.translateContentPathToNodePath( command.getParentPath() ) );

        final Nodes nodes = new GetNodesByParentService( session, getNodesByParentCommand ).execute();

        final Contents contents = CONTENT_TO_NODE_TRANSLATOR.fromNodes( nodes );

        return CONTENT_HAS_CHILD_POPULATOR.populateHasChild( session, filterHiddenContents( contents ) );
    }

}
