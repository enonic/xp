package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.index.IndexService;

public class RenameNodeService
    extends AbstractNodeService
{
    private final EntityId entityId;

    private final NodeName nodeName;

    private final IndexService indexService;

    public RenameNodeService( final Session session, final IndexService indexService, final EntityId entityId, final NodeName nodeName )
    {
        super( session );
        this.entityId = entityId;
        this.nodeName = nodeName;
        this.indexService = indexService;
    }

    public boolean execute()
        throws Exception

    {
        final Node existingNode = getNodeById( this.entityId );

        if ( existingNode == null )
        {
            final ContentId contentId = ContentId.from( this.entityId.toString() );
            throw new ContentNotFoundException( contentId );
        }

        final boolean moved = nodeJcrDao.moveNode( existingNode.path().asAbsolute(),
                                                   new NodePath( existingNode.parent().asAbsolute(), this.nodeName ) );

        session.save();

        this.indexService.indexNode( getNodeById( this.entityId ) );

        return moved;
    }

    private Node getNodeById( final EntityId entityId )
    {
        return new GetNodeByIdService( session, entityId ).execute();
    }
}
