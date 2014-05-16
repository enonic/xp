package com.enonic.wem.core.content.attachment;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;


final class GetAttachmentCommand
{
    private static final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    private NodeService nodeService;

    private String attachmentName;

    private ContentId contentId;

    Attachment execute()
    {
        try
        {
            final EntityId entityId = EntityId.from( this.contentId );
            final Node node = nodeService.getById( entityId, ContentConstants.DEFAULT_WORKSPACE);

            final com.enonic.wem.api.entity.Attachment entityAttachment = node.attachments().getAttachment( this.attachmentName );
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
            throw new ContentNotFoundException( this.contentId );
        }
    }

    GetAttachmentCommand nodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
        return this;
    }

    GetAttachmentCommand attachmentName( final String attachmentName )
    {
        this.attachmentName = attachmentName;
        return this;
    }

    GetAttachmentCommand contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }
}
