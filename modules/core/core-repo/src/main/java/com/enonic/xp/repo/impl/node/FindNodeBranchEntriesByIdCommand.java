package com.enonic.xp.repo.impl.node;

import com.google.common.base.Stopwatch;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.AclFilterBuilderFactory;
import com.enonic.xp.repo.impl.index.query.NodeQueryResult;

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

    public NodeBranchEntries execute()
    {
        final Context context = ContextAccessor.current();

        final NodeBranchEntries.Builder allResultsBuilder = NodeBranchEntries.create();

        final NodeIds nodeIds = getNodeIds( context );

        final Stopwatch getBranchTimer = Stopwatch.createStarted();
        allResultsBuilder.addAll( this.storageService.getBranchNodeVersions( nodeIds, InternalContext.from( context ) ) );
        System.out.println( "GetBranchTimer: " + getBranchTimer.stop() );

        return allResultsBuilder.build();
    }

    private NodeIds getNodeIds( final Context context )
    {
        final NodeQueryResult result = this.searchService.query( NodeQuery.create().
            addQueryFilters( Filters.create().
                add( ValueFilter.create().
                    fieldName( NodeIndexPath.ID.getPath() ).
                    addValues( this.ids.getAsStrings() ).
                    build() ).
                build() ).
            from( 0 ).
            size( ids.getSize() ).
            batchSize( 10000 ).
            searchMode( SearchMode.SCAN ).
            addQueryFilter( AclFilterBuilderFactory.create( context.getAuthInfo().getPrincipals() ) ).
            setOrderExpressions( this.orderExpressions ).
            build(), InternalContext.from( ContextAccessor.current() ) );

        return result.getNodeIds();
    }


    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeIds ids;

        private OrderExpressions orderExpressions;

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
