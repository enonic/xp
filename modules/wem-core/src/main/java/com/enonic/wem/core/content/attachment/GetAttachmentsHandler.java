package com.enonic.wem.core.content.attachment;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.attachment.GetAttachments;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.GetNodeByIdParams;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;
import com.enonic.wem.core.content.serializer.ThumbnailAttachmentSerializer;


public class GetAttachmentsHandler
    extends CommandHandler<GetAttachments>
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

            command.setResult( attachmentsBuilder.build() );
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
