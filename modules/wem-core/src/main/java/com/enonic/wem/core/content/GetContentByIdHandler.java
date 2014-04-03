package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByIdHandler
    extends CommandHandler<GetContentById>
{
    private NodeService nodeService;

    @Inject
    private ContentTypeService contentTypeService;

    @Override
    public void handle()
        throws Exception
    {
        command.setResult( new GetContentByIdService( this.context, this.command, this.nodeService, this.contentTypeService ).execute() );
    }

    @Inject
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
