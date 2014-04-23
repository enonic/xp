package com.enonic.wem.core.entity;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.entity.dao.MoveNodeArguments;
import com.enonic.wem.core.entity.dao.NodeElasticsearchDao;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.index.IndexService;

final class RenameNodeCommand
{
    private RenameNodeParams params;

    private IndexService indexService;

    private NodeElasticsearchDao nodeElasticsearchDao;

    Node execute()
    {
        this.params.validate();

        try
        {
            return doExecute();
        }
        catch ( final Exception e )
        {
            throw Exceptions.newRutime( "Error renaming node" ).withCause( e );
        }
    }

    private Node doExecute()
        throws Exception
    {
        final EntityId entityId = params.getEntityId();
        final Node nodeToBeRenamed = nodeElasticsearchDao.getById( entityId );

        if ( nodeToBeRenamed == null )
        {
            throw new NodeNotFoundException( "Node with id " + entityId + " not found" );
        }

        final Nodes children = nodeElasticsearchDao.getByParent( nodeToBeRenamed.path() );

        final NodePath parentPath = nodeToBeRenamed.parent().asAbsolute();

        final Node renamedNode = doMoveNode( parentPath, params.getNodeName(), params.getEntityId() );

        moveNodesToNewParentPath( children, renamedNode.path() );

        return renamedNode;
    }

    private void moveNodesToNewParentPath( final Nodes nodes, final NodePath newParentPath )
    {
        for ( final Node node : nodes )
        {
            final Node movedNode = doMoveNode( newParentPath, node.name(), node.id() );

            final Nodes children = nodeElasticsearchDao.getByParent( node.path() );

            if ( children != null && children.isNotEmpty() )
            {
                moveNodesToNewParentPath( children, movedNode.path().asAbsolute() );
            }
        }
    }

    private Node doMoveNode( final NodePath newParentPath, final NodeName newNodeName, final EntityId id )
    {
        final MoveNodeArguments moveChildArgument = MoveNodeArguments.newMoveNode().
            name( newNodeName ).
            parentPath( newParentPath ).
            updater( UserKey.superUser() ).
            nodeToMove( id ).
            build();

        nodeElasticsearchDao.move( moveChildArgument );

        final Node movedNode = nodeElasticsearchDao.getById( id );

        indexService.indexNode( movedNode );

        return movedNode;
    }

    RenameNodeCommand params( RenameNodeParams params )
    {
        this.params = params;
        return this;
    }

    RenameNodeCommand indexService( final IndexService indexService )
    {
        this.indexService = indexService;
        return this;
    }

    RenameNodeCommand nodeElasticsearchDao( final NodeElasticsearchDao nodeElasticsearchDao )
    {
        this.nodeElasticsearchDao = nodeElasticsearchDao;
        return this;
    }
}
