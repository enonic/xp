package com.enonic.wem.core.entity;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeAlreadyExistException;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.entity.dao.MoveNodeArguments;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;

final class RenameNodeCommand
    extends AbstractNodeCommand
{
    private final RenameNodeParams params;

    private final ElasticsearchIndexService indexService;

    private final NodeDao nodeDao;

    private RenameNodeCommand( Builder builder )
    {
        super( builder );

        this.params = builder.params;
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
    }

    Node execute()
    {
        this.params.validate();

        try
        {
            return doExecute();
        }
        catch ( NodeAlreadyExistException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw Exceptions.newRutime( "Error renaming node" ).withCause( e );
        }
    }

    private Node doExecute()
        throws Exception
    {
        final EntityId entityId = params.getEntityId();
        final Workspace workspace = this.context.getWorkspace();

        final Node nodeToBeRenamed = nodeDao.getById( entityId, workspace );

        if ( nodeToBeRenamed == null )
        {
            throw new NodeNotFoundException( "Node with id " + entityId + " not found" );
        }

        final NodePath parentPath = nodeToBeRenamed.parent().asAbsolute();
        final NodePath targetPath = new NodePath( parentPath, params.getNodeName() );
        final EntityId existingNodeId = getNodeIdFromPath( targetPath );
        if ( ( existingNodeId != null ) && !nodeToBeRenamed.id().equals( existingNodeId ) )
        {
            throw new NodeAlreadyExistException( targetPath );
        }

        final Nodes children = nodeDao.getByParent( nodeToBeRenamed.path(), workspace );

        final Node renamedNode = doMoveNode( parentPath, params.getNodeName(), params.getEntityId() );

        moveNodesToNewParentPath( children, renamedNode.path() );

        return renamedNode;
    }

    private EntityId getNodeIdFromPath( final NodePath path )
    {
        try
        {
            final Node existingNode = nodeDao.getByPath( path, this.context.getWorkspace() );
            return existingNode == null ? null : existingNode.id();
        }
        catch ( final NodeNotFoundException e )
        {
            return null;
        }
    }

    private void moveNodesToNewParentPath( final Nodes nodes, final NodePath newParentPath )
    {
        for ( final Node node : nodes )
        {
            final Node movedNode = doMoveNode( newParentPath, node.name(), node.id() );

            final Nodes children = nodeDao.getByParent( node.path(), this.context.getWorkspace() );

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

        final Workspace workspace = this.context.getWorkspace();

        nodeDao.move( moveChildArgument, workspace );

        final Node movedNode = nodeDao.getById( id, workspace );

        indexService.index( movedNode, workspace );

        return movedNode;
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        public Builder( final Context context )
        {
            super( context );
        }

        private RenameNodeParams params;

        private ElasticsearchIndexService indexService;

        private NodeDao nodeDao;

        public Builder params( RenameNodeParams params )
        {
            this.params = params;
            return this;
        }

        public Builder indexService( final ElasticsearchIndexService indexService )
        {
            this.indexService = indexService;
            return this;
        }

        public Builder nodeDao( final NodeDao nodeDao )
        {
            this.nodeDao = nodeDao;
            return this;
        }

        public RenameNodeCommand build()
        {
            return new RenameNodeCommand( this );
        }

    }
}
