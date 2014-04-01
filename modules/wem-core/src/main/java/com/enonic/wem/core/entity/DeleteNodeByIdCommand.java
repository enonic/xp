package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.DeleteNodeByIdParams;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;

public class DeleteNodeByIdCommand
{
    private IndexService indexService;

    private Session session;

    private DeleteNodeByIdParams params;

    public Node execute()
    {
        this.params.validate();
        return doExecute();
    }

    private Node doExecute()
    {
        final EntityId entityId = params.getId();
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        final Node nodeToDelete = nodeJcrDao.getNodeById( entityId );

        nodeJcrDao.deleteNodeById( entityId );
        JcrSessionHelper.save( session );

        indexService.deleteEntity( entityId );

        return nodeToDelete;
    }

    public DeleteNodeByIdCommand indexService( final IndexService indexService )
    {
        this.indexService = indexService;
        return this;
    }

    public DeleteNodeByIdCommand params( final DeleteNodeByIdParams params )
    {
        this.params = params;
        return this;
    }

    public DeleteNodeByIdCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
