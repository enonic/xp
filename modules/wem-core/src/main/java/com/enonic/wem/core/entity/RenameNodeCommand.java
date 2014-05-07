package com.enonic.wem.core.entity;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.elastic.ElasticsearchIndexService;
import com.enonic.wem.core.entity.dao.MoveNodeArguments;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;

final class RenameNodeCommand
{
    private RenameNodeParams params;

    private ElasticsearchIndexService indexService;

    private NodeDao nodeDao;

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
        final Node nodeToBeRenamed = nodeDao.getById( entityId );

        if ( nodeToBeRenamed == null )
        {
            throw new NodeNotFoundException( "Node with id " + entityId + " not found" );
        }

        final Nodes children = nodeDao.getByParent( nodeToBeRenamed.path() );

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

            final Nodes children = nodeDao.getByParent( node.path() );

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

        nodeDao.move( moveChildArgument );

        final Node movedNode = nodeDao.getById( id );

        indexService.index( movedNode );

        return movedNode;
    }

    RenameNodeCommand params( RenameNodeParams params )
    {
        this.params = params;
        return this;
    }

    RenameNodeCommand indexService( final ElasticsearchIndexService indexService )
    {
        this.indexService = indexService;
        return this;
    }

    RenameNodeCommand nodeDao( final NodeDao nodeDao )
    {
        this.nodeDao = nodeDao;
        return this;
    }
}
