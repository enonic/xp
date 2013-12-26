package com.enonic.wem.core.content.attachment;

import javax.jcr.Session;

import com.enonic.wem.api.command.content.attachment.GetAttachments;
import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFound;
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
        final Session session = context.getJcrSession();

        try
        {
            final Node node = new GetNodeByIdService( session, new GetNodeById( EntityId.from( command.getContentId() ) ) ).execute();
            final Attachments.Builder attachmentsBuilder = Attachments.newAttachments();

            for ( com.enonic.wem.api.entity.Attachment entityAttachment : node.attachments() )
            {
                attachmentsBuilder.add( CONTENT_ATTACHMENT_NODE_TRANSLATOR.toContentAttachment( entityAttachment ) );
            }

            command.setResult( attachmentsBuilder.build() );
        }
        catch ( NoEntityWithIdFound e )
        {
            throw new ContentNotFoundException( command.getContentId() );
        }
    }
}
