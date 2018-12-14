package com.enonic.xp.repo.impl.node;

import java.util.List;

import com.google.common.collect.Iterables;

import com.enonic.xp.context.Context;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.security.acl.Permission;

abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    private final boolean allowDeleteRootNode;

    private static final int BATCH_SIZE = 20;

    AbstractDeleteNodeCommand( final Builder builder )
    {
        super( builder );
        this.allowDeleteRootNode = builder.allowDeleteRoot;
    }

    NodeBranchEntries deleteNodeWithChildren( final Node node, final Context context )
    {
        return deleteNodeWithChildren( node, context, null );
    }

    NodeBranchEntries deleteNodeWithChildren( final Node node, final Context context, final DeleteNodeListener deleteNodeListener )
    {
        if ( node.isRoot() && !allowDeleteRootNode )
        {
            throw new OperationNotPermittedException( "Not allowed to delete root-node" );
        }

        doRefresh();

        final NodeBranchEntries nodesToBeDeleted = newResolveNodesToDelete( node );

        final NodeIds nodeIds = NodeIds.from( nodesToBeDeleted.getKeys() );

        final boolean allHasPermissions = NodesHasPermissionResolver.create( this ).
            nodeIds( nodeIds ).
            permission( Permission.DELETE ).
            build().
            execute();

        if ( !allHasPermissions )
        {
            throw new NodeAccessException( context.getAuthInfo().getUser(), node.path(), Permission.DELETE );
        }

        for ( final List<NodeId> keysBatch : Iterables.partition( nodeIds, BATCH_SIZE ) )
        {
            this.nodeStorageService.delete( NodeIds.from( keysBatch ), InternalContext.from( context ) );

            if ( deleteNodeListener != null )
            {
                deleteNodeListener.nodesDeleted( keysBatch.size() );
            }
        }

        doRefresh();

        return nodesToBeDeleted;
    }

    private void doRefresh()
    {
        RefreshCommand.create().
            refreshMode( RefreshMode.ALL ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }


    private NodeBranchEntries newResolveNodesToDelete( final Node node )
    {
        final FindNodeIdsByParentCommand command = FindNodeIdsByParentCommand.create( this ).
            parentPath( node.path() ).
            recursive( true ).
            childOrder( ChildOrder.reversePath() ).
            size( NodeSearchService.GET_ALL_SIZE_FLAG ).
            build();

        final FindNodesByParentResult result = command.execute();

        final NodeIds nodeIds = NodeIds.create().
            addAll( result.getNodeIds() ).
            add( node.id() ).
            build();

        return FindNodeBranchEntriesByIdCommand.
            create( command ).
            ids( nodeIds ).
            orderExpressions( ChildOrder.reversePath().getOrderExpressions() ).
            build().
            execute();
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeCommand.Builder<B>
    {
        boolean allowDeleteRoot = false;

        public Builder()
        {
            super();
        }

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        @SuppressWarnings("unchecked")
        public B allowDeleteRoot( final boolean allowDeleteRoot )
        {
            this.allowDeleteRoot = allowDeleteRoot;
            return (B) this;
        }

    }
}
