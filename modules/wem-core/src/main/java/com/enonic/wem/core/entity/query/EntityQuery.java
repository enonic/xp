package com.enonic.wem.core.entity.query;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.filter.Filters;
import com.enonic.wem.core.entity.EntityId;

public class EntityQuery
{
    private final QueryExpr query;

    private final ImmutableSet<EntityId> ids;

    private final Filters postFilters;

    private final Filters queryFilters;

    private final ImmutableSet<AggregationQuery> aggregationQueries;

    private final ImmutableSet<OrderExpr> orderBys;

    private final int from;

    private final int size;

    @SuppressWarnings("unchecked")
    public EntityQuery( final Builder builder )
    {
        this.query = builder.query;
        this.postFilters = builder.postFilters.build();
        this.queryFilters = builder.queryFilters.build();
        this.orderBys = query != null ? ImmutableSet.copyOf( query.getOrderSet() ) : ImmutableSet.<OrderExpr>of();
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
    public Filters getPostFilters()
    {
        return postFilters;
    }

    // These are filters to be applied into query, and considered in facets also
    public Filters getQueryFilters()
    {
        return queryFilters;
    }

    public ImmutableSet<OrderExpr> getOrderBys()
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

        private final Filters.Builder postFilters = Filters.create();

        private final Filters.Builder queryFilters = Filters.create();

        private final Set<AggregationQuery> aggregationQueries = Sets.newHashSet();

        private final Set<EntityId> ids = Sets.newHashSet();

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
        public T addPostFilter( final Filter filter )
        {
            this.postFilters.add( filter );
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addQueryFilter( final Filter queryFilter )
        {
            this.queryFilters.add( queryFilter );
            return (T) this;
        }


        @SuppressWarnings("unchecked")
        public T addQueryFilters( final Filters queryFilters )
        {
            this.queryFilters.addAll( queryFilters.getSet() );
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
