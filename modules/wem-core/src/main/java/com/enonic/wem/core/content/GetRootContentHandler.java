package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.command.entity.GetNodesByParent;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.entity.GetNodesByParentService;
import com.enonic.wem.core.entity.dao.NodeJcrDao;

public class GetRootContentHandler
    extends CommandHandler<GetRootContent>
{
    private ContentDao contentDao;

    private ContentNodeTranslator CONTENT_TO_NODE_TRANSLATOR = new ContentNodeTranslator();


    @Override
    public void handle()
        throws Exception
    {

        //final Session jcrSession = context.getJcrSession();
        //final Contents contents = contentDao.findChildContent( ContentPath.ROOT, jcrSession );

        GetNodesByParent getNodesByParentCommand = new GetNodesByParent( NodeJcrDao.CONTENT_ROOT_NODE.asAbsolute() );

        final Nodes rootNodes = new GetNodesByParentService( this.context.getJcrSession(), getNodesByParentCommand ).execute();

        command.setResult( CONTENT_TO_NODE_TRANSLATOR.fromNodes( rootNodes ) );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
