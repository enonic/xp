package com.enonic.wem.api.entity.query;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.filter.Filter;

public class EntityQuery
{
    private final QueryExpr query;

    private final ImmutableSet<EntityId> ids;

    private final ImmutableSet<Filter> filters;

    private final ImmutableSet<Filter> queryFilters;

    private final ImmutableSet<AggregationQuery> aggregationQueries;

    private final ImmutableList<OrderExpr> orderBys;

    private final int from;

    private final int size;

    public EntityQuery( final Builder builder )
    {
        this.query = builder.query;
        this.filters = ImmutableSet.copyOf( builder.filters );
        this.queryFilters = ImmutableSet.copyOf( builder.queryFilters );
        this.orderBys = query != null ? ImmutableList.copyOf( query.getOrderList() ) : ImmutableList.<OrderExpr>of();
        this.size = builder.size;
        this.from = builder.from;
        this.aggregationQueries = ImmutableSet.copyOf( builder.aggregationQueries );
        this.ids = ImmutableSet.copyOf( builder.ids );
    }

    public static Builder newEntityQuery()
    {
        return new Builder();
    }

    public QueryExpr getQuery()
    {
        return query;
    }

    // These are filters that are applied outside query, not considered in facets
    public ImmutableSet<Filter> getFilters()
    {
        return filters;
    }

    // These are filters to be applied into query, and considered in facets also
    public ImmutableSet<Filter> getQueryFilters()
    {
        return queryFilters;
    }

    public ImmutableList<OrderExpr> getOrderBys()
    {
        return orderBys;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public ImmutableSet<AggregationQuery> getAggregationQueries()
    {
        return aggregationQueries;
    }

    public static class Builder<T extends Builder>
    {
        private QueryExpr query;

        private Set<Filter> filters = Sets.newHashSet();

        private Set<Filter> queryFilters = Sets.newHashSet();

        private Set<AggregationQuery> aggregationQueries = Sets.newHashSet();

        private Set<EntityId> ids = Sets.newHashSet();

        private int from = 0;

        private int size = 10;

        @SuppressWarnings("unchecked")
        public T query( final QueryExpr query )
        {
            this.query = query;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addId( final EntityId id )
        {
            this.ids.add( id );
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addFilter( final Filter filter )
        {
            this.filters.add( filter );
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addQueryFilter( final Filter queryFilter )
        {
            this.queryFilters.add( queryFilter );
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addQueryFilters( final Set<Filter> queryFilters )
        {
            this.queryFilters.addAll( queryFilters );
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T from( final int from )
        {
            this.from = from;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T size( final int size )
        {
            this.size = size;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addAggregationQueries( final Set<AggregationQuery> aggregationQueries )
        {
            this.aggregationQueries.addAll( aggregationQueries );
            return (T) this;
        }

        public EntityQuery build()
        {
            return new EntityQuery( this );
        }
    }

}
