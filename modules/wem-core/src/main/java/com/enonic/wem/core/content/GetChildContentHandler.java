package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.GetNodesByParentService;


public class GetChildContentHandler
    extends CommandHandler<GetChildContent>
{
    private final static ContentHasChildPopulator CONTENT_HAS_CHILD_POPULATOR = new ContentHasChildPopulator();

    private ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final GetNodesByParent getNodesByParentCommand =
            new GetNodesByParent( ContentNodeHelper.translateContentPathToNodePath( command.getParentPath() ) );

        final Nodes nodes = new GetNodesByParentService( this.context.getJcrSession(), getNodesByParentCommand ).execute();

        final Contents contents = CONTENT_TO_NODE_TRANSLATOR.fromNodes( nodes );

        command.setResult( CONTENT_HAS_CHILD_POPULATOR.populateHasChild( this.context.getJcrSession(), contents ) );
    }

}
