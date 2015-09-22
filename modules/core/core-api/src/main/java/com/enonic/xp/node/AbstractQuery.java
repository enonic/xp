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
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;

public class AbstractQuery
    implements Query
{
    protected final static int DEFAULT_QUERY_SIZE = 10;

    private final QueryExpr query;

    private final Filters postFilters;

    private final Filters queryFilters;

    private final AggregationQueries aggregationQueries;

    private final int from;

    private final int size;

    private final SearchMode searchMode;

    private final ImmutableList<OrderExpr> orderBys;

    @SuppressWarnings("unchecked")
    protected AbstractQuery( Builder builder )
    {
        this.query = builder.query;
        this.from = builder.from;
        this.size = builder.size;
        this.searchMode = builder.searchMode;
        this.aggregationQueries = AggregationQueries.fromCollection( ImmutableSet.copyOf( builder.aggregationQueries ) );
        this.orderBys = query != null ? ImmutableList.copyOf( query.getOrderList() ) : ImmutableList.<OrderExpr>of();
        this.postFilters = builder.postFilters.build();
        this.queryFilters = builder.queryFilters.build();
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

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public SearchMode getSearchMode()
    {
        return searchMode;
    }

    public static abstract class Builder<B extends Builder>
    {
        private QueryExpr query;

        private final Filters.Builder postFilters = Filters.create();

        private final Filters.Builder queryFilters = Filters.create();

        private final Set<AggregationQuery> aggregationQueries = Sets.newHashSet();

        private int from;

        private int size = DEFAULT_QUERY_SIZE;

        private List<OrderExpr> orderBys = Lists.newLinkedList();

        private SearchMode searchMode = SearchMode.SEARCH;

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
            this.queryFilters.addAll( queryFilters.getSet() );
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
        public B addAggregationQuery( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
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
        public B addOrderBy( final OrderExpr orderExpr )
        {
            this.orderBys.add( orderExpr );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B setOrderBys( final List<OrderExpr> orderExpr )
        {
            this.orderBys = orderExpr;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B searchMode( SearchMode searchMode )
        {
            this.searchMode = searchMode;
            return (B) this;
        }
    }
}
