package com.enonic.xp.lib.node;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.JsonToFilterMapper;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.query.suggester.SuggestionQueries;
import com.enonic.xp.script.ScriptValue;

abstract class AbstractFindNodesQueryHandler
    extends AbstractNodeHandler
{
    private final Integer start;

    private final Integer count;

    private final ScriptValue query;

    private final ScriptValue sort;

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

        final QueryExpr queryExpr = QueryExpr.from( buildConstraintExpr(), buildOrderExpr() );
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

    private ConstraintExpr buildConstraintExpr()
    {
        if ( query == null )
        {
            return QueryParser.parseCostraintExpression( "" );
        }
        else if ( query.isValue() )
        {
            return QueryParser.parseCostraintExpression( query.getValue( String.class ) );
        }
        else if ( query.isObject() )
        {
            return DslExpr.from( PropertyTree.fromMap( query.getMap() ) );
        }
        throw new IllegalArgumentException( "query must be a String or JSON object" );
    }

    private List<OrderExpr> buildOrderExpr()
    {
        if ( sort == null )
        {
            return List.of();
        }
        if ( sort.isValue() )
        {
            return QueryParser.parseOrderExpressions( sort.getValue( String.class ) );
        }
        else if ( sort.isObject() )
        {
            return List.of( DslOrderExpr.from( PropertyTree.fromMap( sort.getMap() ) ) );
        }
        else if ( sort.isArray() )
        {
            return sort.getArray()
                .stream()
                .map( expr -> DslOrderExpr.from( PropertyTree.fromMap( expr.getMap() ) ) )
                .collect( Collectors.toList() );
        }

        throw new IllegalArgumentException( "sort must be a String, JSON object or array of JSON objects" );
    }


    public static class Builder<B extends Builder>
        extends AbstractNodeHandler.Builder<B>
    {
        private Integer start;

        private Integer count;

        private ScriptValue query;

        private ScriptValue sort;

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
        public B query( final ScriptValue val )
        {
            query = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B sort( final ScriptValue val )
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
