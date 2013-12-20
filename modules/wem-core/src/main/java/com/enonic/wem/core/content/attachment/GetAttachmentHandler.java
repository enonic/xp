package com.enonic.wem.core.content.attachment;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.content.attachment.GetAttachment;
import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.ContentAttachmentNodeTranslator;
import com.enonic.wem.core.content.ContentNodeHelper;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;
import com.enonic.wem.core.entity.GetNodeByIdService;
import com.enonic.wem.core.entity.GetNodeByPathService;


public class GetAttachmentHandler
    extends CommandHandler<GetAttachment>
{
    private AttachmentDao attachmentDao;

    final ContentAttachmentNodeTranslator CONTENT_ATTACHMENT_NODE_TRANSLATOR = new ContentAttachmentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();

        final Node node;

        if ( command.getContentId() != null )
        {
            GetNodeById getNodeByIdCommand = new GetNodeById( EntityId.from( command.getContentId().toString() ) );
            node = new GetNodeByIdService( session, getNodeByIdCommand ).execute();
        }
        else
        {
            GetNodeByPath getNodeByPathCommand =
                new GetNodeByPath( ContentNodeHelper.translateContentPathToNodePath( command.getContentPath() ) );
            node = new GetNodeByPathService( session, getNodeByPathCommand ).execute();
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

    @Inject
    public void setAttachmentDao( final AttachmentDao attachmentDao )
    {
        this.attachmentDao = attachmentDao;
    }
}
