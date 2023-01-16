package com.enonic.xp.repo.impl.node;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    private static final int BATCH_SIZE = 20;

    private final DeleteNodeListener deleteNodeListener;

    private final RefreshMode refresh;

    AbstractDeleteNodeCommand( final Builder<?> builder )
    {
        super( builder );
        this.deleteNodeListener = Objects.requireNonNullElseGet( builder.deleteNodeListener, EmptyDeleteNodeListener::new );
        this.refresh = builder.refresh;
    }

    NodeBranchEntries deleteNodeWithChildren( final Node node )
    {
        if ( node.isRoot() )
        {
            throw new OperationNotPermittedException( "Not allowed to delete root-node" );
        }

        refresh( RefreshMode.ALL );

        final Context context = ContextAccessor.current();

        final Context adminContext = ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();

        final SearchResult childrenSearchResult = this.nodeSearchService.query( NodeQuery.create()
                                                                                    .query( QueryExpr.from( CompareExpr.like(
                                                                                        FieldExpr.from( NodeIndexPath.PATH ),
                                                                                        ValueExpr.string( node.path() + "/*" ) ) ) )
                                                                                    .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                                                                                    .build(), SingleRepoSearchSource.from( adminContext ) );
        final NodeIds nodeIds = NodeIds.create().add( node.id() ).addAll( NodeIds.from( childrenSearchResult.getIds() ) ).build();

        final boolean allHasPermissions = NodesHasPermissionResolver.create( this ).nodeIds( nodeIds )
            .permission( Permission.DELETE )
            .build()
            .execute();

        if ( !allHasPermissions )
        {
            throw new NodeAccessException( context.getAuthInfo().getUser(), node.path(), Permission.DELETE );
        }

        final NodeBranchEntries nodeBranchEntries =
            this.nodeStorageService.getBranchNodeVersions( nodeIds, InternalContext.from( context ) );

        final List<NodeBranchEntry> list = nodeBranchEntries.getSet()
            .stream()
            .sorted( Comparator.comparing( NodeBranchEntry::getNodePath ).reversed() )
            .collect( Collectors.toList() );

        for ( final List<NodeBranchEntry> batch : Iterables.partition( list, BATCH_SIZE ) )
        {
            this.nodeStorageService.delete( batch, InternalContext.from( context ) );

            deleteNodeListener.nodesDeleted( batch.size() );
        }

        refresh( refresh );

        return NodeBranchEntries.from( list );
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeCommand.Builder<B>
    {
        private DeleteNodeListener deleteNodeListener;

        private RefreshMode refresh;

        Builder()
        {
            super();
        }

        Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder<B> deleteNodeListener( final DeleteNodeListener deleteNodeListener )
        {
            this.deleteNodeListener = deleteNodeListener;
            return this;
        }

        public Builder<B> refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

    }

    private static class EmptyDeleteNodeListener
        implements DeleteNodeListener
    {
        @Override
        public void nodesDeleted( final int count )
        {
        }

        @Override
        public void totalToDelete( final int count )
        {
        }
    }
}
