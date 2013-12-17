package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.GetNodesByParentService;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetRootContentHandler
    extends CommandHandler<GetRootContent>
{
    private final static ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    private final static ContentHasChildPopulator CONTENT_HAS_CHILD_POPULATOR = new ContentHasChildPopulator();

    @Override
    public void handle()
        throws Exception
    {

        final GetNodesByParent getNodesByParentCommand = new GetNodesByParent( NodeJcrDao.CONTENT_ROOT_NODE.asAbsolute() );

        final Nodes rootNodes = new GetNodesByParentService( this.context.getJcrSession(), getNodesByParentCommand ).execute();

        final Contents contents = CONTENT_TO_NODE_TRANSLATOR.fromNodes( rootNodes );

        command.setResult( CONTENT_HAS_CHILD_POPULATOR.populateHasChild( this.context.getJcrSession(), contents ) );
    }
}
