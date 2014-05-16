package com.enonic.wem.core.content.attachment;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;
import com.enonic.wem.core.content.serializer.ThumbnailAttachmentSerializer;


final class GetAttachmentsCommand
{
    private static final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    private NodeService nodeService;

    private ContentId contentId;

    Attachments execute()
    {
        try
        {
            final EntityId entityId = EntityId.from( this.contentId );
            final Node node = nodeService.getById( entityId, ContentConstants.DEFAULT_WORKSPACE );
            final Attachments.Builder attachmentsBuilder = Attachments.builder();

            for ( com.enonic.wem.api.entity.Attachment entityAttachment : node.attachments() )
            {
                final boolean isThumbnail = entityAttachment.name().equals( ThumbnailAttachmentSerializer.THUMB_NAME );

                if ( !isThumbnail )
                {
                    final Attachment attachment = CONTENT_ATTACHMENT_NODE_TRANSLATOR.toContentAttachment( entityAttachment );

                    attachmentsBuilder.add( attachment );
                }
            }

            return attachmentsBuilder.build();
        }
        catch ( NoEntityWithIdFoundException e )
        {
            throw new ContentNotFoundException( this.contentId );
        }
    }

    GetAttachmentsCommand nodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
        return this;
    }

    GetAttachmentsCommand contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }
}
