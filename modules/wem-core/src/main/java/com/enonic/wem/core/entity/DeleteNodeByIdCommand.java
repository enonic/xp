package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.DeleteNodeByIdParams;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.util.Exceptions;

public class DeleteNodeByIdCommand
{
    private IndexService indexService;

    private Session session;

    private DeleteNodeByIdParams params;

    public Node execute()
    {
        this.params.validate();

        try
        {
            return doExecute();
        }
        catch ( final Exception e )
        {
            throw Exceptions.newRutime( "Error deleting node" ).withCause( e );
        }
    }

    private Node doExecute()
        throws Exception
    {
        final EntityId entityId = params.getId();
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        final Node nodeToDelete = nodeJcrDao.getNodeById( entityId );

        nodeJcrDao.deleteNodeById( entityId );
        session.save();

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
