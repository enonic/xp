package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;

public class GetNodeByIdService
    extends AbstractNodeService
{
    private final EntityId entityId;

    public GetNodeByIdService( final Session session, final EntityId entityId )
    {
        super( session );
        this.entityId = entityId;
    }

    public Node execute()
    {
        return nodeJcrDao.getNodeById( this.entityId );
    }
}
