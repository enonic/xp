package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodePath;
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

import static java.util.Objects.requireNonNull;

/**
 * Finds all branch entries below a parent path by querying the branch storage index,
 * so that storage operations never depend on the search index.
 */
final class FindNodeBranchEntriesByParentCommand
    extends AbstractNodeCommand
{
    private final NodePath parentPath;

    private final OrderExpr.Direction pathOrder;

    private final Permission requiredPermission;

    private FindNodeBranchEntriesByParentCommand( final Builder builder )
    {
        super( builder );
        this.parentPath = builder.parentPath;
        this.pathOrder = builder.pathOrder;
        this.requiredPermission = builder.requiredPermission;
    }

    static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    NodeBranchEntries execute()
    {
        final InternalContext context = InternalContext.from( ContextAccessor.current() );

        refresh( RefreshMode.STORAGE );

        final NodeBranchQuery query = NodeBranchQuery.create()
            .query( QueryExpr.from( createParentExpr() ) )
            .addQueryFilter( ValueFilter.create()
                                 .fieldName( BranchIndexPath.BRANCH_NAME.getPath() )
                                 .addValue( ValueFactory.newString( context.getBranch().getValue() ) )
                                 .build() )
            .addOrderBy( FieldOrderExpr.create( BranchIndexPath.PATH, pathOrder ) )
            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
            .build();

        final NodeBranchEntries entries =
            NodeBranchQueryResultFactory.create( this.nodeSearchService.query( query, context.getRepositoryId() ) );

        return filterByPermission( entries, context );
    }

    private CompareExpr createParentExpr()
    {
        return parentPath.isRoot()
            ? CompareExpr.neq( FieldExpr.from( BranchIndexPath.PATH ), ValueExpr.string( NodePath.ROOT.toString() ) )
            : CompareExpr.like( FieldExpr.from( BranchIndexPath.PATH ), ValueExpr.string( parentPath + "/*" ) );
    }

    private NodeBranchEntries filterByPermission( final NodeBranchEntries entries, final InternalContext context )
    {
        if ( requiredPermission == null || context.getPrincipalKeys().contains( RoleKeys.ADMIN ) )
        {
            return entries;
        }

        final NodeBranchEntries.Builder filtered = NodeBranchEntries.create();
        for ( final NodeBranchEntry entry : entries )
        {
            final AccessControlList permissions = this.nodeStorageService.getNodePermissions( entry.getNodeVersionKey(), context );
            if ( NodePermissionsResolver.hasPermission( context.getPrincipalKeys(), requiredPermission, permissions ) )
            {
                filtered.add( entry );
            }
        }
        return filtered.build();
    }

    static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath parentPath;

        private OrderExpr.Direction pathOrder = OrderExpr.Direction.ASC;

        private Permission requiredPermission;

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        Builder pathOrder( final OrderExpr.Direction pathOrder )
        {
            this.pathOrder = pathOrder;
            return this;
        }

        Builder requiredPermission( final Permission requiredPermission )
        {
            this.requiredPermission = requiredPermission;
            return this;
        }

        FindNodeBranchEntriesByParentCommand build()
        {
            validate();
            return new FindNodeBranchEntriesByParentCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( parentPath, "parentPath is required" );
        }
    }
}
