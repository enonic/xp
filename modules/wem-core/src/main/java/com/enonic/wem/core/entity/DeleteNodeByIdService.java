package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.core.index.IndexService;

public class DeleteNodeByIdService
    extends AbstractNodeService
{
    private final IndexService indexService;

    private final EntityId entityId;

    public DeleteNodeByIdService( final Session session, final IndexService indexService, final EntityId entityId )
    {
        super( session );
        this.indexService = indexService;
        this.entityId = entityId;
    }

    public void execute()
        throws Exception
    {
        nodeJcrDao.deleteNodeById( entityId );
        session.save();

        indexService.deleteEntity( entityId );
    }
}
