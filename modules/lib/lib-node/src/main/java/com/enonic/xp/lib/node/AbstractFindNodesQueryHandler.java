package com.enonic.xp.lib.node;

import java.util.List;
import java.util.Map;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.JsonToFilterMapper;
import com.enonic.xp.lib.common.JsonToPropertyTreeTranslator;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.query.suggester.SuggestionQueries;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.util.JsonHelper;

abstract class AbstractFindNodesQueryHandler
    extends AbstractNodeHandler
{
    private final Integer start;

    private final Integer count;

    private final Object query;

    private final String sort;

    private final List<Map<String, Object>> filters;

    private final Map<String, Object> aggregations;

    private final Map<String, Object> suggestions;

    private final Map<String, Object> highlight;

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
        this.suggestions = builder.suggestions;
        this.highlight = builder.highlight;
        this.explain = builder.explain;
    }

    NodeQuery createNodeQuery()
    {
        final int start = valueOrDefault( this.start, 0 );
        final int count = valueOrDefault( this.count, 10 );
        if ( this.query != null )
        {
            Object test = valueOrDefault( this.query, "" );
        }
        final Object query = valueOrDefault( this.query, "" );
        final String sort = valueOrDefault( this.sort, "" ).trim();

        final List<OrderExpr> orderExpressions = QueryParser.parseOrderExpressions( sort );
        final QueryExpr queryExpr = QueryExpr.from( parseQueryExpr( query ), orderExpressions );
        final Filters filters = JsonToFilterMapper.create( this.filters );

        final AggregationQueries aggregations = new QueryAggregationParams().getAggregations( this.aggregations );
        final SuggestionQueries suggestions = new QuerySuggestionParams().getSuggestions( this.suggestions );

        final HighlightQuery highlight = new QueryHighlightParams().getHighlightQuery( this.highlight );

        return NodeQuery.create()
            .from( start )
            .size( count )
            .addAggregationQueries( aggregations )
            .addSuggestionQueries( suggestions )
            .highlight( highlight )
            .query( queryExpr )
            .addQueryFilters( filters )
            .explain( this.explain )
            .build();
    }

    private ConstraintExpr parseQueryExpr( final Object query )
    {
        ConstraintExpr constraintExpr;
        if ( query instanceof String )
        {
            constraintExpr = QueryParser.parseCostraintExpression( (String) query );
        }
        else if ( query instanceof ScriptValue )
        {
            final ScriptValue value = (ScriptValue) query;

            if ( value.isValue() )
            {
                constraintExpr = QueryParser.parseCostraintExpression( ( (ScriptValue) query ).getValue().toString() );
            }
            else
            {
                final PropertyTree dslExpression = JsonToPropertyTreeTranslator.translate( JsonHelper.from( value.getMap() ) );
                constraintExpr = DslExpr.from( dslExpression );
            }
        }
        else
        {
            throw new IllegalArgumentException( "invalid query: " + query );
        }

        return constraintExpr;

    }

    public static class Builder<B extends Builder>
        extends AbstractNodeHandler.Builder<B>
    {
        private Integer start;

        private Integer count;

        private Object query;

        private String sort;

        private List<Map<String, Object>> filters;

        private Map<String, Object> aggregations;

        private Map<String, Object> suggestions;

        private Map<String, Object> highlight;

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
        public B query( final Object val )
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
        public B suggestions( final Map<String, Object> val )
        {
            suggestions = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B highlight( final Map<String, Object> val )
        {
            highlight = val;
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
