package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.command.entity.RenameNode;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;

public class RenameNodeService
    extends NodeService
{
    private final RenameNode command;

    public RenameNodeService( final Session session, final RenameNode renameNode )
    {
        super( session );
        this.command = renameNode;
    }

    public boolean execute()
        throws Exception

    {
        final GetNodeById getNodeByIdCommand = new GetNodeById( command.getId() );
        final Node existingNode = new GetNodeByIdService( session, getNodeByIdCommand ).execute();

        if ( existingNode == null )
        {
            final ContentId contentId = ContentId.from( command.getId().toString() );
            throw new ContentNotFoundException( contentId );
        }

        final boolean moved = nodeJcrDao.moveNode( existingNode.path().asAbsolute(),
                                                   new NodePath( existingNode.parent().asAbsolute(), command.getNodeName() ) );

        session.save();
        return moved;
    }
}
