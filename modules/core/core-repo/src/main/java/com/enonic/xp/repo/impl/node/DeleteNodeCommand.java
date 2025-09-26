package com.enonic.xp.repo.impl.node;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Iterables;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
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
        this.deleteNodeListener = Objects.requireNonNullElse( builder.deleteNodeListener, count -> {
        } );
        this.refresh = builder.refresh;
    }

    public NodeBranchEntries execute()
    {
        if ( Node.ROOT_UUID.equals( this.nodeId ) || NodePath.ROOT.equals( this.nodePath ) )
        {
            throw new OperationNotPermittedException( "Not allowed to delete root-node" );
        }

        final Context context = ContextAccessor.current();
        final InternalContext internalContext = InternalContext.from( context );
        final AuthenticationInfo authInfo = context.getAuthInfo();

        final NodeBranchEntry node = nodeId != null
            ? this.nodeStorageService.getBranchNodeVersion( nodeId, internalContext )
            : this.nodeStorageService.getBranchNodeVersion( nodePath, internalContext );

        if ( node == null )
        {
            // Node not found in storage, but potentially still in index. Attempt to fixup.
            if ( nodeId != null )
            {
                this.nodeStorageService.deleteFromIndex( nodeId, internalContext );
            }
            return NodeBranchEntries.empty();
        }

        final NodePath effectiveNodePath = node.getNodePath();

        refresh( RefreshMode.STORAGE );

        final NodeBranchEntries childrenBranchEntries = NodeBranchQueryResultFactory.create( this.nodeSearchService.query(
            NodeBranchQuery.create()
                .query( QueryExpr.from(
                    CompareExpr.like( FieldExpr.from( BranchIndexPath.PATH ), ValueExpr.string( effectiveNodePath + "/*" ) ) ) )
                .addQueryFilter( ValueFilter.create()
                                     .fieldName( BranchIndexPath.BRANCH_NAME.getPath() )
                                     .addValue( ValueFactory.newString( internalContext.getBranch().getValue() ) )
                                     .build() )
                .addOrderBy( FieldOrderExpr.create( BranchIndexPath.PATH, OrderExpr.Direction.DESC ) )
                .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                .build(), internalContext.getRepositoryId() ) );

        final NodeBranchEntries nodeBranchEntries = NodeBranchEntries.create().addAll( childrenBranchEntries ).add( node ).build();

        if ( !authInfo.hasRole( RoleKeys.ADMIN ) )
        {
            for ( NodeBranchEntry branchEntry : nodeBranchEntries )
            {
                final AccessControlList nodePermissions =
                    this.nodeStorageService.getNodePermissions( branchEntry.getNodeVersionKey(), internalContext );

                if ( !NodePermissionsResolver.hasPermission( internalContext.getPrincipalsKeys(), Permission.DELETE, nodePermissions ) )
                {
                    throw new NodeAccessException( authInfo.getUser(), effectiveNodePath, Permission.DELETE );
                }
            }
        }

        for ( final List<NodeBranchEntry> batch : Iterables.partition( nodeBranchEntries, BATCH_SIZE ) )
        {
            this.nodeStorageService.delete( batch, internalContext );
            this.deleteNodeListener.nodesDeleted( batch.size() );
        }

        refresh( refresh );

        return nodeBranchEntries;
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
}
