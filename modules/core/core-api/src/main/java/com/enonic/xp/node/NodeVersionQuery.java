package com.enonic.xp.node;

import com.enonic.xp.query.expr.OrderExpressions;

public final class NodeVersionQuery
    extends AbstractQuery
{
    private final NodeId nodeId;

    private NodeVersionQuery( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final NodeVersionQuery source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private NodeId nodeId;

        private Builder()
        {
        }

        private Builder( final NodeVersionQuery source )
        {
            this.addQueryFilters( source.getQueryFilters() ).
                nodeId( source.nodeId ).
                size( source.getSize() ).
                from( source.getFrom() ).
                aggregationQueries( source.getAggregationQueries().getSet() ).
                setOrderExpressions( OrderExpressions.from( source.getOrderBys() ) );

            source.getPostFilters().forEach( this::addPostFilter );
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public NodeVersionQuery build()
        {
            return new NodeVersionQuery( this );
        }
    }
}
