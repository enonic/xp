package com.enonic.xp.repo.impl.node;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

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

    NodeBranchEntries deleteNodeWithChildren( final Node node, final DeleteNodeListener deleteNodeListener )
    {
        if ( node.isRoot() && !allowDeleteRootNode )
        {
            throw new OperationNotPermittedException( "Not allowed to delete root-node" );
        }

        doRefresh();

        final Context context = ContextAccessor.current();

        final Context adminContext = ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();

        final FindNodesByParentResult result = adminContext.callWith( FindNodeIdsByParentCommand.create( this )
                                   .parentPath( node.path() )
                                   .recursive( true )
                                   .build()::execute );

        final NodeIds nodeIds = NodeIds.create().addAll( result.getNodeIds() ).add( node.id() ).build();

        final boolean allHasPermissions = NodesHasPermissionResolver.create( this ).
            nodeIds( nodeIds ).
            permission( Permission.DELETE ).
            build().
            execute();

        final NodeBranchEntries nodeBranchEntries =
            this.nodeStorageService.getBranchNodeVersions( nodeIds, InternalContext.from( context ) );

        if ( !allHasPermissions )
        {
            throw new NodeAccessException( context.getAuthInfo().getUser(), node.path(), Permission.DELETE );
        }

        final List<NodeBranchEntry> list = nodeBranchEntries.getSet()
            .stream()
            .sorted( Comparator.comparing( NodeBranchEntry::getNodePath ).reversed() )
            .collect( Collectors.toList() );

        for ( final List<NodeBranchEntry> batch : Iterables.partition( list, BATCH_SIZE ) )
        {
            final NodeIds nodeIdsBatch =
                NodeIds.from( batch.stream().map( NodeBranchEntry::getNodeId ).collect( ImmutableSet.toImmutableSet() ) );
            this.nodeStorageService.delete( nodeIdsBatch, InternalContext.from( context ) );

            if ( deleteNodeListener != null )
            {
                deleteNodeListener.nodesDeleted( batch.size() );
            }
        }

        doRefresh();

        return NodeBranchEntries.from( list );
    }

    private void doRefresh()
    {
        RefreshCommand.create().
            refreshMode( RefreshMode.ALL ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeCommand.Builder<B>
    {
        boolean allowDeleteRoot = false;

        Builder()
        {
            super();
        }

        Builder( final AbstractNodeCommand source )
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
