package com.enonic.xp.lib.node;

import java.util.Set;

import com.enonic.xp.context.Context;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.node.SearchTargets;

public class MultiRepoNodeHandler
{
    private final SearchTargets searchTargets;

    private final Context context;

    private final NodeService nodeService;

    private MultiRepoNodeHandler( final Builder builder )
    {
        searchTargets = SearchTargets.from( builder.searchTargets );
        context = builder.context;
        nodeService = builder.nodeService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @SuppressWarnings("unused")
    public Object query( final QueryNodeHandlerParams params )
    {
        return execute( FindNodesByMultiNodeQueryHandler.create().
            searchTargets( this.searchTargets ).
            query( params.getQuery() ).
            aggregations( params.getAggregations() ).
            count( params.getCount() ).
            start( params.getStart() ).
            sort( params.getSort() ).
            filters( params.getFilters() ).
            explain( params.isExplain() ).
            nodeService( this.nodeService ).
            build() );
    }

    private Object execute( final AbstractNodeHandler handler )
    {
        return this.context.callWith( handler::execute );
    }

    public static final class Builder
    {
        private Set<SearchTarget> searchTargets;

        private Context context;

        private NodeService nodeService;

        private Builder()
        {
        }

        public Builder searchTargets( final Set<SearchTarget> val )
        {
            searchTargets = val;
            return this;
        }

        public Builder context( final Context val )
        {
            context = val;
            return this;
        }

        public Builder nodeService( final NodeService val )
        {
            nodeService = val;
            return this;
        }

        public MultiRepoNodeHandler build()
        {
            return new MultiRepoNodeHandler( this );
        }
    }
}
