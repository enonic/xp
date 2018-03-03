package com.enonic.xp.lib.node;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.xp.lib.common.JsonToFilterMapper;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.parser.QueryParser;

abstract class AbstractFindNodesQueryHandler
    extends AbstractNodeHandler
{
    private final Integer start;

    private final Integer count;

    private final String query;

    private final String sort;

    private final List<Map<String, Object>> filters;

    private final Map<String, Object> aggregations;

    private final boolean explain;

    AbstractFindNodesQueryHandler( final Builder builder )
    {
        super( builder );
        this.start = builder.start;
        this.count = builder.count;
        this.query = builder.query;
        this.sort = builder.sort;
        this.filters = builder.filters;
        this.aggregations = builder.aggregations;
        this.explain = builder.explain;
    }

    NodeQuery createNodeQuery()
    {
        final int start = valueOrDefault( this.start, 0 );
        final int count = valueOrDefault( this.count, 10 );
        final String query = valueOrDefault( this.query, "" ).trim();
        final String sort = valueOrDefault( this.sort, "" ).trim();

        final List<OrderExpr> orderExpressions = QueryParser.parseOrderExpressions( sort );
        final ConstraintExpr constraintExpr = QueryParser.parseCostraintExpression( query );
        final QueryExpr queryExpr = QueryExpr.from( constraintExpr, orderExpressions );
        final Filters filters = JsonToFilterMapper.create( this.filters );

        final Set<AggregationQuery> aggregations = new QueryAggregationParams().getAggregations( this.aggregations );

        return NodeQuery.create().
            from( start ).
            size( count ).
            aggregationQueries( aggregations ).
            query( queryExpr ).
            addQueryFilters( filters ).
            explain( this.explain ).
            build();
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeHandler.Builder<B>
    {
        private Integer start;

        private Integer count;

        private String query;

        private String sort;

        private List<Map<String, Object>> filters;

        private Map<String, Object> aggregations;

        private boolean explain = false;

        Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B start( final Integer val )
        {
            start = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B count( final Integer val )
        {
            count = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B query( final String val )
        {
            query = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B sort( final String val )
        {
            sort = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B filters( final List<Map<String, Object>> val )
        {
            filters = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B aggregations( final Map<String, Object> val )
        {
            aggregations = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B explain( final boolean explain )
        {
            this.explain = explain;
            return (B) this;
        }
    }

}
