package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.command.entity.DeleteNodeByPath;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.index.IndexService;

public class DeleteNodeByPathService
    extends NodeService
{
    private IndexService indexService;

    private DeleteNodeByPath command;

    public DeleteNodeByPathService( final Session session, final IndexService indexService, final DeleteNodeByPath command )
    {
        super( session );
        this.indexService = indexService;
        this.command = command;
    }

    public Node execute()
        throws Exception
    {
        final Node nodeToDelete = nodeJcrDao.getNodeByPath( command.getPath() );

        nodeJcrDao.deleteNodeByPath( command.getPath() );
        session.save();

        indexService.deleteEntity( nodeToDelete.id() );
        return nodeToDelete;
    }

}
