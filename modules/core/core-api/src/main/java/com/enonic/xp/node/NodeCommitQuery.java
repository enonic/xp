package com.enonic.xp.node;

import com.enonic.xp.query.expr.OrderExpressions;

public final class NodeCommitQuery
    extends AbstractQuery
{

    private NodeCommitQuery( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final NodeCommitQuery source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private Builder()
        {
        }

        private Builder( final NodeCommitQuery source )
        {
            this.addQueryFilters( source.getQueryFilters() ).
                size( source.getSize() ).
                from( source.getFrom() ).
                aggregationQueries( source.getAggregationQueries().getSet() ).
                setOrderExpressions( OrderExpressions.from( source.getOrderBys() ) );

            source.getPostFilters().forEach( this::addPostFilter );
        }

        public NodeCommitQuery build()
        {
            return new NodeCommitQuery( this );
        }
    }
}
