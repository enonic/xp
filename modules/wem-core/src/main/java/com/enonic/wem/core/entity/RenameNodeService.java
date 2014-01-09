package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.api.command.entity.RenameNode;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.index.IndexService;

public class RenameNodeService
    extends NodeService
{
    private final RenameNode command;

    private final IndexService indexService;

    public RenameNodeService( final Session session, final IndexService indexService, final RenameNode renameNode )
    {
        super( session );
        this.command = renameNode;
        this.indexService = indexService;
    }

    public boolean execute()
        throws Exception

    {
        final Node existingNode = getNodeById( command.getId() );

        if ( existingNode == null )
        {
            final ContentId contentId = ContentId.from( command.getId().toString() );
            throw new ContentNotFoundException( contentId );
        }

        final boolean moved = nodeJcrDao.moveNode( existingNode.path().asAbsolute(),
                                                   new NodePath( existingNode.parent().asAbsolute(), command.getNodeName() ) );

        session.save();

        this.indexService.indexNode( getNodeById( command.getId() ) );

        return moved;
    }

    private Node getNodeById( final EntityId entityId )
    {
        final GetNodeById getNodeByIdCommand = new GetNodeById( entityId );
        return new GetNodeByIdService( session, getNodeByIdCommand ).execute();
    }
}
