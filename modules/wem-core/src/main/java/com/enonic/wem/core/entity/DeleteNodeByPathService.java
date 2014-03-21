package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.core.index.IndexService;

public class DeleteNodeByPathService
    extends AbstractNodeService
{
    private IndexService indexService;

    private NodePath nodePath;

    public DeleteNodeByPathService( final Session session, final IndexService indexService, final NodePath nodePath )
    {
        super( session );
        this.indexService = indexService;
        this.nodePath = nodePath;
    }

    public Node execute()
        throws Exception
    {
        final Node nodeToDelete = nodeJcrDao.getNodeByPath( nodePath );

        nodeJcrDao.deleteNodeByPath( nodePath );
        session.save();

        indexService.deleteEntity( nodeToDelete.id() );
        return nodeToDelete;
    }

}
