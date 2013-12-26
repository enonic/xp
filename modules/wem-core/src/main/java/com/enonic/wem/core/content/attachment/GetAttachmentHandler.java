package com.enonic.wem.core.content.attachment;

import com.enonic.wem.api.command.content.attachment.GetAttachment;
import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityWithIdFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;
import com.enonic.wem.core.entity.GetNodeByIdService;


public class GetAttachmentHandler
    extends CommandHandler<GetAttachment>
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
        catch ( NoEntityWithIdFound e )
        {
            throw new ContentNotFoundException( command.getContentId() );
        }
    }

}
