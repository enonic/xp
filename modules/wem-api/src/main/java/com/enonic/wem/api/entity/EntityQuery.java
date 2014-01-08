package com.enonic.wem.api.entity;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.facet.FacetQuery;
import com.enonic.wem.api.query.filter.Filter;

public class EntityQuery
{
    private final QueryExpr query;

    private final ImmutableSet<Filter> filters;

    private final ImmutableSet<Filter> queryFilters;

    private final ImmutableSet<FacetQuery> facetQueries;

    private final ImmutableList<OrderExpr> orderBys;

    private final int from;

    private final int size;

    public EntityQuery( final Builder builder )
    {
        this.query = builder.query;
        this.filters = ImmutableSet.copyOf( builder.filters );
        this.queryFilters = ImmutableSet.copyOf( builder.queryFilters );
        this.facetQueries = ImmutableSet.copyOf( builder.facetQueries );
        this.orderBys = query != null ? ImmutableList.copyOf( query.getOrderList() ) : ImmutableList.<OrderExpr>of();
        this.size = builder.size;
        this.from = builder.from;
    }

    public static Builder newQuery()
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

    public ImmutableSet<FacetQuery> getFacetQueries()
    {
        return facetQueries;
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

    public static class Builder<T>
    {
        private QueryExpr query;

        private Set<Filter> filters = Sets.newHashSet();

        private Set<Filter> queryFilters = Sets.newHashSet();

        private Set<FacetQuery> facetQueries = Sets.newHashSet();

        private int from = 0;

        private int size = 10;

        public Builder query( final QueryExpr query )
        {
            this.query = query;
            return this;
        }

        public Builder addFilter( final Filter filter )
        {
            this.filters.add( filter );
            return this;
        }

        public Builder addQueryFilter( final Filter queryFilter )
        {
            this.queryFilters.add( queryFilter );
            return this;
        }

        public Builder addFacet( final FacetQuery facetQuery )
        {
            this.facetQueries.add( facetQuery );
            return this;
        }

        public Builder from( final int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public EntityQuery build()
        {
            return new EntityQuery( this );
        }
    }

}
