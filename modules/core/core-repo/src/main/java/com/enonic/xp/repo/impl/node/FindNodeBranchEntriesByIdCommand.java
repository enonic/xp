package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AclFilterBuilderFactory;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class FindNodeBranchEntriesByIdCommand
    extends AbstractNodeCommand
{
    private final NodeIds ids;

    private final OrderExpressions orderExpressions;

    private FindNodeBranchEntriesByIdCommand( final Builder builder )
    {
        super( builder );
        ids = builder.ids;
        orderExpressions = builder.orderExpressions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public NodeBranchEntries execute()
    {
        final Context context = ContextAccessor.current();

        final NodeBranchEntries.Builder allResultsBuilder = NodeBranchEntries.create();

        final NodeIds nodeIds = getNodeIds( context );

        allResultsBuilder.addAll(
            this.nodeStorageService.getBranchNodeVersions( nodeIds, !this.orderExpressions.isEmpty(), InternalContext.from( context ) ) );

        return allResultsBuilder.build();
    }

    private NodeIds getNodeIds( final Context context )
    {
        if ( this.ids.isEmpty() )
        {
            return NodeIds.empty();
        }

        final NodeQuery.Builder queryBuilder = NodeQuery.create().
            addQueryFilters( Filters.create().
                add( IdFilter.create().
                    fieldName( NodeIndexPath.ID.getPath() ).
                    values( this.ids ).
                    build() ).
                build() ).
            from( 0 ).
            size( ids.getSize() ).
            batchSize( 10000 ).
            addQueryFilter( AclFilterBuilderFactory.create( context.getAuthInfo().getPrincipals() ) );

        if ( !this.orderExpressions.isEmpty() )
        {
            queryBuilder.setOrderExpressions( this.orderExpressions );
        }

        final SearchResult result = this.nodeSearchService.query( queryBuilder.
            build(), SingleRepoSearchSource.from( ContextAccessor.current() ) );

        return NodeIds.from( result.getIds() );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeIds ids;

        private OrderExpressions orderExpressions = OrderExpressions.empty();

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder ids( final NodeIds val )
        {
            ids = val;
            return this;
        }

        public Builder orderExpressions( final OrderExpressions val )
        {
            orderExpressions = val;
            return this;
        }

        public FindNodeBranchEntriesByIdCommand build()
        {
            return new FindNodeBranchEntriesByIdCommand( this );
        }
    }
}
