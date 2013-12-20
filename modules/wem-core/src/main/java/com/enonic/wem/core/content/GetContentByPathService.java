package com.enonic.wem.core.content;

import javax.jcr.Session;

import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.GetNodeByPathService;


public class GetContentByPathService
    extends ContentService
{
    private final GetContentByPath command;

    public GetContentByPathService( final Session session, final GetContentByPath command )
    {
        super( session );
        this.command = command;
    }

    public Content execute()
        throws Exception
    {
        final GetNodeByPath getNodeByPathCommand =
            new GetNodeByPath( ContentNodeHelper.translateContentPathToNodePath( command.getPath() ) );

        final Node node = new GetNodeByPathService( session, getNodeByPathCommand ).execute();
        final Content content = CONTENT_TO_NODE_TRANSLATOR.fromNode( node );

        return content;
    }
}
