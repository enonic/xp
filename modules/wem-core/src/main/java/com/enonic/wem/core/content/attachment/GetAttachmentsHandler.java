package com.enonic.wem.core.content.attachment;

import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.attachment.GetAttachments;
import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;
import com.enonic.wem.core.entity.GetNodeByIdService;


public class GetAttachmentsHandler
    extends CommandHandler<GetAttachments>
{
    final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        try
        {
            final Node node =
                new GetNodeByIdService( context.getJcrSession(), new GetNodeById( EntityId.from( command.getContentId() ) ) ).execute();
            final Attachments.Builder attachmentsBuilder = Attachments.builder();

            for ( com.enonic.wem.api.entity.Attachment entityAttachment : node.attachments() )
            {
                final boolean isThumbnail = entityAttachment.name().equals( CreateContent.THUMBNAIL_NAME );

                if ( !isThumbnail || command.isIncludeThumbnail() )
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
}
