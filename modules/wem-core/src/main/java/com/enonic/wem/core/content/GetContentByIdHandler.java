package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.GetNodeByIdService;


public class GetContentByIdHandler
    extends CommandHandler<GetContentById>
{
    private ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final GetNodeById getNodeByIdCommand = new GetNodeById( EntityId.from( command.getId().toString() ) );

        final Node node = new GetNodeByIdService( this.context.getJcrSession(), getNodeByIdCommand ).execute();

        if ( node == null )
        {
            throw new ContentNotFoundException( command.getId() );
        }

        final Content content = CONTENT_TO_NODE_TRANSLATOR.fromNode( node );

        command.setResult( content );
    }
}
