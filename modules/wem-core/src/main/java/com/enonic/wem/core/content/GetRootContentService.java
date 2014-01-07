package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.entity.GetNodesByParentService;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetRootContentService
    extends ContentService
{
    public GetRootContentService( final CommandContext context, @SuppressWarnings("UnusedParameters") final GetRootContent command )
    {
        super( context );
    }

    public Contents execute()
        throws Exception
    {
        final GetNodesByParent getNodesByParentCommand = new GetNodesByParent( NodeJcrDao.CONTENT_ROOT_NODE.asAbsolute() );

        final Nodes rootNodes = new GetNodesByParentService( session, getNodesByParentCommand ).execute();

        final Contents contents = translator.fromNodes( removeNonContentNodes( rootNodes ) );
        return new ChildContentIdsResolver( context ).resolve( contents );
    }
}
