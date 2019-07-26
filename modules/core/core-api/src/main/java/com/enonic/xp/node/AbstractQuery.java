package com.enonic.xp.node;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.query.Query;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.suggester.SuggestionQueries;
import com.enonic.xp.query.suggester.SuggestionQuery;

public class AbstractQuery
    implements Query
{
    private final static int DEFAULT_QUERY_SIZE = 10;

    private final QueryExpr query;

    private final Filters postFilters;

    private final Filters queryFilters;

    private final AggregationQueries aggregationQueries;

    private final SuggestionQueries suggestionQueries;

    private final HighlightQuery highlight;

    private final int from;

    private final int size;

    private final int batchSize;

    private final SearchMode searchMode;

    private final SearchOptimizer searchOptimizer;

    private final ImmutableList<OrderExpr> orderBys;

    private final boolean explain;

    @SuppressWarnings("unchecked")
    protected AbstractQuery( Builder builder )
    {
        this.query = builder.query;
        this.from = builder.from;
        this.size = builder.size;
        this.batchSize = builder.batchSize;
        this.searchMode = builder.searchMode;
        this.aggregationQueries = AggregationQueries.fromCollection( ImmutableSet.copyOf( builder.aggregationQueries ) );
        this.suggestionQueries = SuggestionQueries.fromCollection( ImmutableSet.copyOf( builder.suggestionQueries ) );
        this.highlight = builder.highlight;
        this.orderBys = setOrderExpressions( builder );
        this.postFilters = builder.postFilters.build();
        this.queryFilters = builder.queryFilters.build();
        this.searchOptimizer = builder.searchOptimizer;
        this.explain = builder.explain;
    }

    private ImmutableList<OrderExpr> setOrderExpressions( final Builder builder )
    {
        final List<OrderExpr> orderBys = Lists.newLinkedList();

        if ( query != null )
        {
            orderBys.addAll( query.getOrderList() );
        }

        orderBys.addAll( builder.orderBys );

        return ImmutableList.copyOf( orderBys );
    }

    public ImmutableList<OrderExpr> getOrderBys()
    {
        return orderBys;
    }

    public QueryExpr getQuery()
    {
        return query;
    }

    public Filters getPostFilters()
    {
        return postFilters;
    }

    public Filters getQueryFilters()
    {
        return queryFilters;
    }

    public AggregationQueries getAggregationQueries()
    {
        return aggregationQueries;
    }

    public SuggestionQueries getSuggestionQueries()
    {
        return suggestionQueries;
    }

    public HighlightQuery getHighlight()
    {
        return highlight;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public boolean isExplain()
    {
        return explain;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public SearchMode getSearchMode()
    {
        return searchMode;
    }

    public SearchOptimizer getSearchOptimizer()
    {
        return searchOptimizer;
    }

    public static abstract class Builder<B extends Builder>
    {
        private final Filters.Builder postFilters = Filters.create();

        private final Filters.Builder queryFilters = Filters.create();

        private Set<AggregationQuery> aggregationQueries = Sets.newHashSet();

        private Set<SuggestionQuery> suggestionQueries = Sets.newHashSet();

        private HighlightQuery highlight;

        private QueryExpr query;

        private int from;

        private int size = DEFAULT_QUERY_SIZE;

        private int batchSize = 5_000;

        private final List<OrderExpr> orderBys = Lists.newLinkedList();

        private SearchMode searchMode = SearchMode.SEARCH;

        private SearchOptimizer searchOptimizer = SearchOptimizer.SPEED;

        private boolean explain = false;

        protected Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B query( QueryExpr query )
        {
            this.query = query;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addQueryFilter( final Filter queryFilter )
        {
            this.queryFilters.add( queryFilter );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addQueryFilters( final Filters queryFilters )
        {
            this.queryFilters.addAll( queryFilters.getList() );
            return (B) this;
        }


        @SuppressWarnings("unchecked")
        public B addPostFilter( final Filter filter )
        {
            this.postFilters.add( filter );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addAggregationQueries( final AggregationQueries aggregationQueries )
        {
            this.aggregationQueries.addAll( aggregationQueries.getSet() );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B aggregationQueries( final Set<AggregationQuery> aggregationQueries )
        {
            this.aggregationQueries = aggregationQueries;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addAggregationQuery( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addSuggestionQueries( final SuggestionQueries suggestionQueries )
        {
            this.suggestionQueries.addAll( suggestionQueries.getSet() );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B suggestionQueries( final Set<SuggestionQuery> suggestionQueries )
        {
            this.suggestionQueries = suggestionQueries;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addSuggestionQuery( final SuggestionQuery suggestionQuery )
        {
            this.suggestionQueries.add( suggestionQuery );
            return (B) this;
        }

        public B highlight( final HighlightQuery highlight )
        {
            this.highlight = highlight;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B from( int from )
        {
            this.from = from;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B size( int size )
        {
            this.size = size;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B batchSize( int batchSize )
        {
            this.batchSize = batchSize;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B explain( boolean explain )
        {
            this.explain = explain;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addOrderBy( final OrderExpr orderExpr )
        {
            this.orderBys.add( orderExpr );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B setOrderExpressions( final OrderExpressions orderExpressions )
        {
            orderExpressions.forEach( this.orderBys::add );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B searchMode( SearchMode searchMode )
        {
            this.searchMode = searchMode;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B searchOptimizer( SearchOptimizer searchOptimizer )
        {
            this.searchOptimizer = searchOptimizer;
            return (B) this;
        }
    }
}
