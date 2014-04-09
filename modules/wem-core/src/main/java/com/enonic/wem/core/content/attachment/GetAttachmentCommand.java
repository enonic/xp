package com.enonic.wem.core.content.attachment;

import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.GetAttachmentParams;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;


final class GetAttachmentCommand
{
    final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    private NodeService nodeService;

    private GetAttachmentParams params;

    Attachment execute()
    {
        params.validate();

        return doExecute();
    }

    private Attachment doExecute()
    {
        try
        {
            final EntityId entityId = EntityId.from( params.getContentId() );
            final Node node = nodeService.getById( entityId );

            final com.enonic.wem.api.entity.Attachment entityAttachment = node.attachments().getAttachment( params.getAttachmentName() );
            if ( entityAttachment != null )
            {
                return CONTENT_ATTACHMENT_NODE_TRANSLATOR.toContentAttachment( entityAttachment );
            }
            else
            {
                return null;
            }
        }
        catch ( NoEntityWithIdFoundException e )
        {
            throw new ContentNotFoundException( params.getContentId() );
        }
    }

    GetAttachmentCommand nodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
        return this;
    }

    GetAttachmentCommand params( final GetAttachmentParams params )
    {
        this.params = params;
        return this;
    }
}
