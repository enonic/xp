package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.core.command.CommandHandler;


public class RenameContentHandler
    extends CommandHandler<RenameContent>
{

    @Inject
    private NodeService nodeService;

    @Inject
    private ContentTypeService contentTypeService;

    @Override
    public void handle()
        throws Exception
    {

        final EntityId entityId = EntityId.from( command.getContentId() );
        final NodeName nodeName = NodeName.from( command.getNewName().toString() );
        nodeService.rename( new RenameNodeParams().entityId( entityId ).nodeName( nodeName ) );
        this.context.getJcrSession().save();

        GetContentById getContentByIdCommand = new GetContentById( command.getContentId() );
        final Content renamedContent = new GetContentByIdService( this.context, getContentByIdCommand, this.nodeService, this.contentTypeService ).execute();

        command.setResult( renamedContent );
    }
}
