package com.enonic.wem.core.content.attachment;

import javax.jcr.Session;

import com.enonic.wem.api.command.content.attachment.GetAttachment;
import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;
import com.enonic.wem.core.content.ContentNodeHelper;
import com.enonic.wem.core.entity.GetNodeByIdService;
import com.enonic.wem.core.entity.GetNodeByPathService;


public class GetAttachmentHandler
    extends CommandHandler<GetAttachment>
{
    final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();

        final Node node;

        if ( command.getContentId() != null )
        {
            GetNodeById getNodeByIdCommand = new GetNodeById( EntityId.from( command.getContentId() ) );
            node = new GetNodeByIdService( session, getNodeByIdCommand ).execute();
            if ( node == null )
            {
                throw new ContentNotFoundException( command.getContentId() );
            }
        }
        else
        {
            GetNodeByPath getNodeByPathCommand =
                new GetNodeByPath( ContentNodeHelper.translateContentPathToNodePath( command.getContentPath() ) );
            node = new GetNodeByPathService( session, getNodeByPathCommand ).execute();
            if ( node == null )
            {
                throw new ContentNotFoundException( command.getContentPath() );
            }
        }

        final com.enonic.wem.api.entity.Attachment nodeAttachment = getAttachmentByName( node );

        if ( nodeAttachment != null )
        {
            command.setResult( translateToContentAttachment( nodeAttachment ) );
        }
        else
        {
            command.setResult( null );
        }
    }

    private Attachment translateToContentAttachment( final com.enonic.wem.api.entity.Attachment nodeAttachment )
    {
        final Attachment attachment;
        attachment = CONTENT_ATTACHMENT_NODE_TRANSLATOR.toContentAttachment( nodeAttachment );
        return attachment;
    }

    private com.enonic.wem.api.entity.Attachment getAttachmentByName( final Node node )
    {
        return node.attachments().getAttachment( command.getAttachmentName() );
    }

}
