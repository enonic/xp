package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;

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

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.query );
        }

        public FindNodesByQueryCommand build()
        {
            this.validate();
            return new FindNodesByQueryCommand( this );
        }
    }
}
