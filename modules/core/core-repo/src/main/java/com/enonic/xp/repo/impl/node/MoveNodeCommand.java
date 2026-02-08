package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.MoveNodeListener;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
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
    private final MoveNodeParams params;

    private final MoveNodeListener moveListener;

    private final MoveNodeResult.Builder result;

    private MoveNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.moveListener = Objects.requireNonNullElse( builder.params.getMoveListener(), count -> {
        } );
        this.result = MoveNodeResult.create();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public MoveNodeResult execute()
    {
        final Node existingNode = doGetById( params.getNodeId() );

        if ( existingNode == null )
        {
            throw new NodeNotFoundException( "cannot rename/move node with id [" + params.getNodeId() + "]" );
        }

        if ( existingNode.isRoot() )
        {
            throw new OperationNotPermittedException( "Not allowed to rename/move root-node" );
        }

        final NodeName newNodeName = resolveNodeName( existingNode );

        final NodePath newParentPath = Objects.requireNonNullElseGet( params.getNewParentPath(), existingNode::parentPath );

        final Context context = ContextAccessor.current();

        if ( noChanges( existingNode, newParentPath, newNodeName ) )
        {
            throw new NodeAlreadyExistAtPathException( new NodePath( newParentPath, newNodeName ), context.getRepositoryId(),
                                                       context.getBranch() );
        }

        checkNotMovedToSelfOrChild( existingNode, newParentPath, newNodeName );

        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.MODIFY, existingNode );

        checkContextUserPermissionOrAdmin( newParentPath );

        verifyNoExistingAtNewPath( newParentPath, newNodeName );

        final Context adminContext = ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();

        adminContext.callWith( () -> doMoveNode( newParentPath, newNodeName, params.getNodeId() ) );

        refresh( params.getRefresh() );

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
        return params.getNewNodeName() == null ? existingNode.name() : params.getNewNodeName();
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
                params.getProcessor().process( persistedNode.data(), NodePath.create( newParentPath ).addElement( newNodeName ).build() ) )
            .parentPath( newParentPath )
            .indexConfigDocument( persistedNode.getIndexConfigDocument() )
            .timestamp( Instant.now( CLOCK ) );

        final boolean isTheOriginalMovedNode = persistedNode.id().equals( params.getNodeId() );
        if ( isTheOriginalMovedNode )
        {
            final boolean isRenaming = newParentPath.equals( persistedNode.parentPath() );

            if ( !isRenaming )
            {
                final Node parentNode = doGetByPath( newParentPath );
                if ( parentNode.getChildOrder().isManualOrder() )
                {
                    final long newOrderValue =
                        ResolveInsertOrderValueCommand.create( this ).parentPath( newParentPath ).build().insert( false );
                    nodeToMoveBuilder.manualOrderValue( newOrderValue );
                }
            }
        }

        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );
        final Node movedNode = this.nodeStorageService.store(
            StoreNodeParams.newVersion( nodeToMoveBuilder.build(),
                                        isTheOriginalMovedNode ? params.getVersionAttributes() : params.getChildVersionAttributes() ),
            internalContext ).node();
        this.nodeStorageService.invalidatePath( persistedNode.path(), internalContext );

        this.result.addMovedNode( MoveNodeResult.MovedNode.create().previousPath( persistedNode.path() ).node( movedNode ).build() );

        moveListener.nodesMoved( 1 );

        refresh( RefreshMode.SEARCH );

        final SearchResult children = this.nodeSearchService.query(
            NodeQuery.create().parent( persistedNode.path() ).size( NodeSearchService.GET_ALL_SIZE_FLAG ).build(),
            ReturnFields.from( NodeIndexPath.NAME ), SingleRepoSearchSource.from( internalContext ) );

        for ( final SearchHit nodeBranchEntry : children.getHits() )
        {
            doMoveNode( movedNode.path(),
                        NodeName.from( nodeBranchEntry.getReturnValues().getStringValue( NodeIndexPath.NAME ) ),
                        NodeId.from( nodeBranchEntry.getId() ) );
        }

        return movedNode;
    }

    private void verifyNoExistingAtNewPath( final NodePath newParentPath, final NodeName newNodeName )
    {
        CheckNodeExistsCommand.create( this ).nodePath( new NodePath( newParentPath, newNodeName ) ).throwIfExists().build().execute();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private MoveNodeParams params;

        private Builder()
        {
            super();
        }

        public Builder params( final MoveNodeParams params )
        {
            this.params = params;
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
            Objects.requireNonNull( params, "params cannot be null" );
        }
    }

}
