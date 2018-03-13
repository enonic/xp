package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.MoveNodeParams;
import com.enonic.xp.security.acl.Permission;

public class MoveNodeCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodePath newParentPath;

    private final NodeName newNodeName;

    private final MoveNodeListener moveListener;

    private MoveNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
        this.newParentPath = builder.newParentPath;
        this.newNodeName = builder.newNodeName;
        this.moveListener = builder.moveListener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public MoveNodeResult execute()
    {
        final Node existingNode = doGetById( nodeId );

        if ( existingNode == null )
        {
            throw new NodeNotFoundException( "cannot move node with id [" + nodeId + "]" );
        }

        if ( existingNode.isRoot() )
        {
            throw new OperationNotPermittedException( "Not allowed to move root-node" );
        }

        final NodeName newNodeName = resolveNodeName( existingNode );

        final NodePath newParentPath = resolvePath( existingNode );

        if ( noChanges( existingNode, newParentPath, newNodeName ) )
        {
            return MoveNodeResult.create().
                sourceNode( existingNode ).
                build();
        }

        checkNotMovedToSelfOrChild( existingNode, newParentPath, newNodeName );

        checkContextUserPermissionOrAdmin( existingNode, newParentPath );

        final Node movedNode = doMoveNode( newParentPath, newNodeName, nodeId );

        RefreshCommand.create().
            refreshMode( RefreshMode.ALL ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        return MoveNodeResult.create().
            sourceNode( existingNode ).
            targetNode( movedNode ).
            build();
    }

    private void checkContextUserPermissionOrAdmin( final Node existingSourceNode, final NodePath newParentPath )
    {
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.MODIFY, existingSourceNode );

        final Node newParentNode = GetNodeByPathCommand.create( this ).
            nodePath( newParentPath ).
            build().
            execute();

        if ( newParentNode == null )
        {
            throw new NodeNotFoundException( "Cannot move node to parent with path '" + newParentPath + "', does not exist" );
        }

        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, newParentNode );
    }

    private NodePath resolvePath( final Node existingNode )
    {
        final NodePath newParentPath;
        if ( this.newParentPath == null )
        {
            newParentPath = existingNode.parentPath();
        }
        else
        {
            newParentPath = this.newParentPath;
        }
        return newParentPath;
    }

    private NodeName resolveNodeName( final Node existingNode )
    {
        final NodeName newNodeName;
        if ( this.newNodeName == null )
        {
            newNodeName = existingNode.name();
        }
        else
        {
            newNodeName = this.newNodeName;
        }
        return newNodeName;
    }

    private void checkNotMovedToSelfOrChild( final Node existingNode, final NodePath newParentPath, final NodeName newNodeName )
    {
        if ( newParentPath.equals( existingNode.path() ) || newParentPath.getParentPaths().contains( existingNode.path() ) )
        {
            throw new MoveNodeException( "Not allowed to move content to itself (" + newParentPath + ")",
                                         new NodePath( newParentPath, newNodeName ) );
        }
    }

    private boolean noChanges( final Node existingNode, final NodePath newParentPath, final NodeName newNodeName )
    {
        return existingNode.parentPath().equals( newParentPath ) && existingNode.name().equals( newNodeName );
    }

    private Node doMoveNode( final NodePath newParentPath, final NodeName newNodeName, final NodeId id )
    {
        final Node persistedNode = doGetById( id );

        final SearchResult result = this.nodeSearchService.query( NodeQuery.create().
            parent( persistedNode.path() ).
            from( 0 ).
            size( NodeSearchService.GET_ALL_SIZE_FLAG ).
            build(), SingleRepoSearchSource.from( ContextAccessor.current() ) );

        final NodeBranchEntries nodeBranchEntries = this.nodeStorageService.getBranchNodeVersions( NodeIds.from( result.getIds() ), false,
                                                                                                   InternalContext.from(
                                                                                                       ContextAccessor.current() ) );

        final NodeName nodeName = ( newNodeName != null ) ? newNodeName : persistedNode.name();

        verifyNoExistingAtNewPath( newParentPath, newNodeName );

        final Node.Builder nodeToMoveBuilder = Node.create( persistedNode ).
            name( nodeName ).
            parentPath( newParentPath ).
            indexConfigDocument( persistedNode.getIndexConfigDocument() ).
            timestamp( Instant.now() );

        final Node movedNode;

        final boolean isTheOriginalMovedNode = persistedNode.id().equals( this.nodeId );
        if ( isTheOriginalMovedNode )
        {
            final boolean isRenaming = newParentPath.equals( persistedNode.parentPath() );

            if ( !isRenaming )
            {
                updateStoredNodeProperties( newParentPath, nodeToMoveBuilder );
            }

            movedNode = doStore( nodeToMoveBuilder.build(), false );
        }
        else
        {
            movedNode = doStore( nodeToMoveBuilder.build(), true );
        }

        nodeMoved( 1 );

        for ( final NodeBranchEntry nodeBranchEntry : nodeBranchEntries )
        {
            doMoveNode( nodeToMoveBuilder.build().path(), getNodeName( nodeBranchEntry ), nodeBranchEntry.getNodeId() );
        }

        return movedNode;
    }

    private void updateStoredNodeProperties( final NodePath newParentPath, final Node.Builder nodeToMoveBuilder )
    {
        // when moving a Node "inheritPermissions" must be set to false so the permissions are kept with the transfer
        nodeToMoveBuilder.inheritPermissions( false );

        if ( shouldUpdateManualOrderValue( newParentPath ) )
        {
            final Long newOrderValue = ResolveInsertOrderValueCommand.create( this ).
                parentPath( newParentPath ).
                insertManualStrategy( InsertManualStrategy.FIRST ).
                build().
                execute();

            nodeToMoveBuilder.manualOrderValue( newOrderValue );
        }
    }

    private boolean shouldUpdateManualOrderValue( final NodePath newParentPath )
    {
        boolean updateManualOrderValue = false;

        if ( newParentPath.equals( this.newParentPath ) )
        {
            final Node parent = GetNodeByPathCommand.create( this ).
                nodePath( newParentPath ).
                build().
                execute();

            if ( parent.getChildOrder().isManualOrder() )
            {
                updateManualOrderValue = true;
            }
        }
        return updateManualOrderValue;
    }

    private Node doStore( final Node movedNode, final boolean metadataOnly )
    {
        return this.nodeStorageService.move( MoveNodeParams.create().
            node( movedNode ).
            updateMetadataOnly( metadataOnly ).
            build(), InternalContext.from( ContextAccessor.current() ) );
    }

    private NodeName getNodeName( final NodeBranchEntry nodeBranchEntry )
    {
        return NodeName.from( nodeBranchEntry.getNodePath().getLastElement().toString() );
    }

    private void nodeMoved( final int count )
    {
        if ( moveListener != null )
        {
            moveListener.nodesMoved( count );
        }
    }

    private void verifyNoExistingAtNewPath( final NodePath newParentPath, final NodeName newNodeName )
    {
        final NodePath newNodePath = NodePath.create( newParentPath, newNodeName.toString() ).build();

        final boolean exists = CheckNodeExistsCommand.create( this ).
            nodePath( newNodePath ).
            build().
            execute();

        if ( exists )
        {
            throw new NodeAlreadyExistAtPathException( new NodePath( newParentPath, newNodeName ) );
        }
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private NodePath newParentPath;

        private NodeName newNodeName;

        private MoveNodeListener moveListener;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder id( final NodeId nodeId )
        {
            this.id = nodeId;
            return this;
        }

        public Builder newParent( final NodePath parentNodePath )
        {
            this.newParentPath = parentNodePath;
            return this;
        }

        public Builder newNodeName( final NodeName nodeName )
        {
            this.newNodeName = nodeName;
            return this;
        }

        public Builder moveListener( final MoveNodeListener moveListener )
        {
            this.moveListener = moveListener;
            return this;
        }

        public MoveNodeCommand build()
        {
            validate();
            return new MoveNodeCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( id );

            if ( this.newParentPath == null && this.newNodeName == null )
            {
                throw new IllegalArgumentException( "Must provide either newNodeName or newParentPath" );
            }

        }
    }

}
