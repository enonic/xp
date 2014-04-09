package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;

final class DeleteNodeByIdCommand
{
    private IndexService indexService;

    private Session session;

    private EntityId entityId;

    Node execute()
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        final Node nodeToDelete = nodeJcrDao.getNodeById( entityId );

        nodeJcrDao.deleteNodeById( entityId );
        JcrSessionHelper.save( session );

        indexService.deleteEntity( entityId );

        return nodeToDelete;
    }

    DeleteNodeByIdCommand indexService( final IndexService indexService )
    {
        this.indexService = indexService;
        return this;
    }

    DeleteNodeByIdCommand entityId( final EntityId entityId )
    {
        this.entityId = entityId;
        return this;
    }

    DeleteNodeByIdCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
