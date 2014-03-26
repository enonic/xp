package com.enonic.wem.core.content.attachment;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.attachment.GetAttachment;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.GetNodeByIdParams;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;


public class GetAttachmentHandler
    extends CommandHandler<GetAttachment>
{
    final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    private NodeService nodeService;

    @Override
    public void handle()
        throws Exception
    {
        try
        {
            final EntityId entityId = EntityId.from( command.getContentId() );
            final GetNodeByIdParams params = new GetNodeByIdParams( entityId );
            final Node node = nodeService.getById( params );

            final com.enonic.wem.api.entity.Attachment entityAttachment = node.attachments().getAttachment( command.getAttachmentName() );

            if ( entityAttachment != null )
            {
                command.setResult( CONTENT_ATTACHMENT_NODE_TRANSLATOR.toContentAttachment( entityAttachment ) );
            }
            else
            {
                command.setResult( null );
            }
        }
        catch ( NoEntityWithIdFoundException e )
        {
            throw new ContentNotFoundException( command.getContentId() );
        }
    }

    @Inject
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
