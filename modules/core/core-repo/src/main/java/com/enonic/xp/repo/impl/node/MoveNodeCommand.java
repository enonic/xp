package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class MoveNodeCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodePath newParentPath;

    private final NodeName newNodeName;

    private final RefreshMode refresh;

    private final NodeDataProcessor processor;

    private final MoveNodeListener moveListener;

    private final MoveNodeResult.Builder result;

    private MoveNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
        this.newParentPath = builder.newParentPath;
        this.newNodeName = builder.newNodeName;
        this.moveListener = Objects.requireNonNullElse( builder.moveListener, count -> {
        } );
        this.processor = builder.processor;
        this.refresh = builder.refresh;
        this.result = MoveNodeResult.create();
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
            throw new NodeNotFoundException( "cannot rename/move node with id [" + nodeId + "]" );
        }

        if ( existingNode.isRoot() )
        {
            throw new OperationNotPermittedException( "Not allowed to rename/move root-node" );
        }

        final NodeName newNodeName = resolveNodeName( existingNode );

        final NodePath newParentPath = Objects.requireNonNullElseGet( this.newParentPath, existingNode::parentPath );

        if ( noChanges( existingNode, newParentPath, newNodeName ) )
        {
            return result.build();
        }

        checkNotMovedToSelfOrChild( existingNode, newParentPath, newNodeName );

        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.MODIFY, existingNode );

        checkContextUserPermissionOrAdmin( newParentPath );

        verifyNoExistingAtNewPath( newParentPath, newNodeName );

        final Context context = ContextAccessor.current();

        final Context adminContext = ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();

        adminContext.callWith( () -> doMoveNode( newParentPath, newNodeName, nodeId ) );

        refresh( refresh );

        return result.build();
    }

    private void checkContextUserPermissionOrAdmin( final NodePath newParentPath )
    {
        final Node newParentNode = doGetByPath( newParentPath );

        if ( newParentNode == null )
        {
            throw new NodeNotFoundException( "Cannot move node to parent with path '" + newParentPath + "', does not exist" );
        }

        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, newParentNode );
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

        final Node.Builder nodeToMoveBuilder = Node.create( persistedNode )
            .name( newNodeName )
            .data(
                processor.process( persistedNode.data(), NodePath.create( newParentPath ).addElement( newNodeName.toString() ).build() ) )
            .parentPath( newParentPath )
            .indexConfigDocument( persistedNode.getIndexConfigDocument() )
            .timestamp( Instant.now( CLOCK ) );

        final boolean isTheOriginalMovedNode = persistedNode.id().equals( this.nodeId );
        if ( isTheOriginalMovedNode )
        {
            final boolean isRenaming = newParentPath.equals( persistedNode.parentPath() );

            if ( !isRenaming )
            {
                updateStoredNodeProperties( newParentPath, nodeToMoveBuilder );
            }
        }

        final Node movedNode = this.nodeStorageService.store(
            StoreNodeParams.create().node( nodeToMoveBuilder.build() ).movedFrom( persistedNode.path() ).build(),
            InternalContext.from( ContextAccessor.current() ) ).node();

        this.result.addMovedNode( MoveNodeResult.MovedNode.create().previousPath( persistedNode.path() ).node( movedNode ).build() );

        moveListener.nodesMoved( 1 );

        refresh( RefreshMode.SEARCH );

        final SearchResult children = this.nodeSearchService.query(
            NodeQuery.create().parent( persistedNode.path() ).size( NodeSearchService.GET_ALL_SIZE_FLAG ).build(),
            ReturnFields.from( NodeIndexPath.NAME ), SingleRepoSearchSource.from( ContextAccessor.current() ) );

        for ( final SearchHit nodeBranchEntry : children.getHits() )
        {
            doMoveNode( nodeToMoveBuilder.build().path(),
                        NodeName.from( (String) nodeBranchEntry.getField( NodeIndexPath.NAME.toString() ).getSingleValue() ),
                        NodeId.from( nodeBranchEntry.getId() ) );
        }

        return movedNode;
    }

    private void updateStoredNodeProperties( final NodePath newParentPath, final Node.Builder nodeToMoveBuilder )
    {
        if ( newParentPath.equals( this.newParentPath ) )
        {
            final Node parentNode = doGetByPath( newParentPath );
            if ( parentNode.getChildOrder().isManualOrder() )
            {

                final Long newOrderValue = ResolveInsertOrderValueCommand.create( this )
                    .parentPath( newParentPath )
                    .insertManualStrategy( InsertManualStrategy.FIRST )
                    .build()
                    .execute();

                nodeToMoveBuilder.manualOrderValue( newOrderValue );
            }
        }
    }

    private void verifyNoExistingAtNewPath( final NodePath newParentPath, final NodeName newNodeName )
    {
        CheckNodeExistsCommand.create( this ).nodePath( new NodePath( newParentPath, newNodeName ) ).throwIfExists().build().execute();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private NodePath newParentPath;

        private NodeName newNodeName;

        private NodeDataProcessor processor = ( n, p ) -> n;

        private MoveNodeListener moveListener;

        private RefreshMode refresh;

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

        public Builder processor( final NodeDataProcessor processor )
        {
            this.processor = processor;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
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
            Preconditions.checkNotNull( id, "id must be set." );
            Preconditions.checkNotNull( processor, "processor must be set." );

            if ( this.newParentPath == null && this.newNodeName == null )
            {
                throw new IllegalArgumentException( "Must provide either newNodeName or newParentPath" );
            }

        }
    }

}
