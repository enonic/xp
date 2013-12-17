package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.command.entity.RenameNode;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.entity.GetNodeByIdService;
import com.enonic.wem.core.entity.RenameNodeService;


public class RenameContentHandler
    extends CommandHandler<RenameContent>
{
    private ContentDao contentDao;

    private AttachmentDao attachmentDao;

    private final static ContentNodeTranslator CONTENT_NODE_TRANSLATOR = new ContentNodeTranslator();

    @Override
    public void handle()
        throws Exception
    {

        final EntityId entityId = EntityId.from( command.getContentId() );
        final RenameNode renameNodeCommand = new RenameNode( entityId, NodeName.from( command.getNewName().toString() ) );

        new RenameNodeService( this.context.getJcrSession(), renameNodeCommand ).execute();
        this.context.getJcrSession().save();

        final GetNodeById getNodeByIdCommand = new GetNodeById( entityId );
        final Node renamedNode = new GetNodeByIdService( this.context.getJcrSession(), getNodeByIdCommand ).execute();

        command.setResult( CONTENT_NODE_TRANSLATOR.fromNode( renamedNode ) );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Inject
    public void setAttachmentDao( final AttachmentDao attachmentDao )
    {
        this.attachmentDao = attachmentDao;
    }
}
