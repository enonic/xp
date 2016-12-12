package com.enonic.xp.lib.node;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.xp.lib.node.mapper.NodeQueryResultMapper;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.script.ScriptValue;

@SuppressWarnings("unused")
public final class FindNodesByQueryHandler
    extends AbstractNodeHandler
{
    private Integer start;

    private Integer count;

    private String query;

    private String sort;

    private Map<String, Object> aggregations;

    private List<String> contentTypes;

    private FindNodesByQueryHandler( final Builder builder )
    {
        super( builder );
        setStart( builder.start );
        setCount( builder.count );
        setQuery( builder.query );
        setSort( builder.sort );
        aggregations = builder.aggregations;
        contentTypes = builder.contentTypes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Object execute()
    {
        final int start = valueOrDefault( this.start, 0 );
        final int count = valueOrDefault( this.count, 10 );
        final String query = valueOrDefault( this.query, "" ).trim();
        final String sort = valueOrDefault( this.sort, "" ).trim();

        final List<OrderExpr> orderExpressions = QueryParser.parseOrderExpressions( sort );
        final ConstraintExpr constraintExpr = QueryParser.parseCostraintExpression( query );
        final QueryExpr queryExpr = QueryExpr.from( constraintExpr, orderExpressions );

        final Set<AggregationQuery> aggregations = new QueryAggregationParams().getAggregations( this.aggregations );

        final NodeQuery nodeQuery = NodeQuery.create().
            from( start ).
            size( count ).
            aggregationQueries( aggregations ).
            query( queryExpr ).
            build();

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        return convert( result );
    }


    private NodeQueryResultMapper convert( final FindNodesByQueryResult findQueryResult )
    {
        return new NodeQueryResultMapper( findQueryResult );
    }

    public void setStart( final Integer start )
    {
        this.start = start;
    }

    public void setCount( final Integer count )
    {
        this.count = count;
    }

    public void setQuery( final String query )
    {
        this.query = query;
    }

    public void setSort( final String sort )
    {
        this.sort = sort;
    }

    public void setAggregations( final ScriptValue value )
    {
        this.aggregations = value != null ? value.getMap() : null;
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private Integer start;

        private Integer count;

        private String query;

        private String sort;

        private Map<String, Object> aggregations;

        private List<String> contentTypes;

        private Builder()
        {
        }

        public Builder start( final Integer val )
        {
            start = val;
            return this;
        }

        public Builder count( final Integer val )
        {
            count = val;
            return this;
        }

        public Builder query( final String val )
        {
            query = val;
            return this;
        }

        public Builder sort( final String val )
        {
            sort = val;
            return this;
        }

        public Builder aggregations( final Map<String, Object> val )
        {
            aggregations = val;
            return this;
        }

        public Builder contentTypes( final List<String> val )
        {
            contentTypes = val;
            return this;
        }

        public FindNodesByQueryHandler build()
        {
            return new FindNodesByQueryHandler( this );
        }
    }
}
