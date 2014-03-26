package com.enonic.wem.core.content;


import javax.inject.Inject;

import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.command.CommandHandler;


public class DeleteContentHandler
    extends CommandHandler<DeleteContent>
{
    private NodeService nodeService;

    @Override
    public void handle()
        throws Exception
    {
        command.setResult( new DeleteContentService( this.context, this.command, this.nodeService ).execute() );
    }

    @Inject
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
