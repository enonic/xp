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
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
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

public class DeleteNodeCommand
    extends AbstractNodeCommand
{
    private static final int BATCH_SIZE = 20;

    private final DeleteNodeListener deleteNodeListener;

    private final NodeId nodeId;

    private final NodePath nodePath;

    private final RefreshMode refresh;

    private DeleteNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.nodePath = builder.nodePath;
        this.deleteNodeListener = Objects.requireNonNullElseGet( builder.deleteNodeListener, EmptyDeleteNodeListener::new );
        this.refresh = builder.refresh;
    }

    public NodeBranchEntries execute()
    {
        if ( this.nodeId == Node.ROOT_UUID || this.nodePath == NodePath.ROOT )
        {
            throw new OperationNotPermittedException( "Not allowed to delete root-node" );
        }

        final Context context = ContextAccessor.current();

        final NodeBranchEntry node = nodeId != null
            ? this.nodeStorageService.getBranchNodeVersion( nodeId, InternalContext.from( context ) )
            : this.nodeStorageService.getBranchNodeVersion( nodePath, InternalContext.from( context ) );

        if ( node == null )
        {
            return NodeBranchEntries.empty();
        }
        final Context adminContext = ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .build();

        refresh( RefreshMode.SEARCH );
        final SearchResult childrenSearchResult = this.nodeSearchService.query( NodeQuery.create()
                                                                                    .query( QueryExpr.from( CompareExpr.like(
                                                                                        FieldExpr.from( NodeIndexPath.PATH ),
                                                                                        ValueExpr.string( node.getNodePath() + "/*" ) ) ) )
                                                                                    .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                                                                                    .build(), SingleRepoSearchSource.from( adminContext ) );
        final NodeIds nodeIds = NodeIds.create().add( node.getNodeId() ).addAll( NodeIds.from( childrenSearchResult.getIds() ) ).build();

        final boolean allHasPermissions = NodesHasPermissionResolver.create( this ).nodeIds( nodeIds )
            .permission( Permission.DELETE )
            .build()
            .execute();

        if ( !allHasPermissions )
        {
            throw new NodeAccessException( context.getAuthInfo().getUser(), node.getNodePath(), Permission.DELETE );
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

    public static Builder create()
    {
        return new DeleteNodeCommand.Builder();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private DeleteNodeListener deleteNodeListener;

        private RefreshMode refresh;

        private NodeId nodeId;

        private NodePath nodePath;

        private Builder()
        {
            super();
        }

        public Builder deleteNodeListener( final DeleteNodeListener deleteNodeListener )
        {
            this.deleteNodeListener = deleteNodeListener;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public DeleteNodeCommand build()
        {
            this.validate();
            return new DeleteNodeCommand( this );
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
