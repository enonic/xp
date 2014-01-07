package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.command.entity.RenameNode;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.entity.RenameNodeService;


public class RenameContentHandler
    extends CommandHandler<RenameContent>
{
    @Override
    public void handle()
        throws Exception
    {

        final EntityId entityId = EntityId.from( command.getContentId() );
        final RenameNode renameNodeCommand = new RenameNode( entityId, NodeName.from( command.getNewName().toString() ) );

        new RenameNodeService( this.context.getJcrSession(), renameNodeCommand ).execute();
        this.context.getJcrSession().save();

        GetContentById getContentByIdCommand = new GetContentById( command.getContentId() );
        final Content renamedContent = new GetContentByIdService( this.context, getContentByIdCommand ).execute();

        command.setResult( renamedContent );
    }

}
