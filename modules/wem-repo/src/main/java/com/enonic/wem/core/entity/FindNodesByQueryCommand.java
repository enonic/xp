package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.query.expr.OrderExpressions;
import com.enonic.wem.repo.NodeQuery;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.NodeQueryResult;
import com.enonic.wem.repo.FindNodesByQueryResult;
import com.enonic.wem.repo.Nodes;

public class FindNodesByQueryCommand
    extends AbstractNodeCommand
{
    private final NodeQuery query;

    private FindNodesByQueryCommand( Builder builder )
    {
        super( builder );
        query = builder.query;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindNodesByQueryResult execute()
    {

        final NodeQueryResult nodeQueryResult = queryService.find( query, IndexContext.from( ContextAccessor.current() ) );

        final Nodes nodes = doGetByIds( nodeQueryResult.getNodeIds(), OrderExpressions.from( query.getOrderBys() ), true );

        return FindNodesByQueryResult.create().
            hits( nodeQueryResult.getHits() ).
            totalHits( nodeQueryResult.getTotalHits() ).
            aggregations( nodeQueryResult.getAggregations() ).
            nodes( nodes ).
            build();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeQuery query;

        private Builder()
        {
            super();
        }

        public Builder query( NodeQuery query )
        {
            this.query = query;
            return this;
        }

        public FindNodesByQueryCommand build()
        {
            return new FindNodesByQueryCommand( this );
        }
    }
}
