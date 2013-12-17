package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.entity.GetNodesByParentService;


public class GetChildContentHandler
    extends CommandHandler<GetChildContent>
{
    @Inject
    private ContentDao contentDao;

    private ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        //Contents result = doGetContents( command.getParentPath(), context );
        //command.setResult( result );

        GetNodesByParent getNodesByParentCommand =
            new GetNodesByParent( ContentNodeHelper.translateContentPathToNodePath( command.getParentPath() ) );

        final Nodes nodes = new GetNodesByParentService( this.context.getJcrSession(), getNodesByParentCommand ).execute();

        command.setResult( CONTENT_TO_NODE_TRANSLATOR.fromNodes( nodes ) );

    }

    private Contents doGetContents( final ContentPath parentPath, final CommandContext context )
    {
        return contentDao.findChildContent( parentPath, context.getJcrSession() );
    }
}
