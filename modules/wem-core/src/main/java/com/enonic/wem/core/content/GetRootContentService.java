package com.enonic.wem.core.content;

import javax.jcr.Session;

import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.GetNodesByParentService;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

class GetRootContentService
extends ContentService
{
    private final static ContentHasChildPopulator CONTENT_HAS_CHILD_POPULATOR = new ContentHasChildPopulator();

    private final GetRootContent command;

    GetRootContentService( final Session session, final GetRootContent command )
    {
        super( session );
        this.command = command;
    }

    Contents execute()
    throws Exception
    {
        final GetNodesByParent getNodesByParentCommand = new GetNodesByParent( NodeJcrDao.CONTENT_ROOT_NODE.asAbsolute() );

        final Nodes rootNodes = new GetNodesByParentService( session, getNodesByParentCommand ).execute();

        final Contents contents = CONTENT_TO_NODE_TRANSLATOR.fromNodes( rootNodes );

        return CONTENT_HAS_CHILD_POPULATOR.populateHasChild( session, filterHiddenContents( contents ) );
    }
}
