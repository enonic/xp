package com.enonic.wem.query;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.query.expr.OrderExpr;
import com.enonic.wem.query.expr.QueryExpr;
import com.enonic.wem.query.filter.Filter;

public class EntityQuery
{
    private final QueryExpr query;

    private final ImmutableSet<Filter> filters;

    private final ImmutableSet<Filter> queryFilters;

    private final ImmutableSet<Facet> facets;

    private final ImmutableList<OrderExpr> orderBys;

    public EntityQuery( final Builder builder )
    {
        this.query = builder.query;
        this.filters = ImmutableSet.copyOf( builder.filters );
        this.queryFilters = ImmutableSet.copyOf( builder.queryFilters );
        this.facets = ImmutableSet.copyOf( builder.facets );
        this.orderBys = query != null ? ImmutableList.copyOf( query.getOrderList() ) : ImmutableList.<OrderExpr>of();
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

    public ImmutableSet<Facet> getFacets()
    {
        return facets;
    }

    public ImmutableList<OrderExpr> getOrderBys()
    {
        return orderBys;
    }

    public static class Builder
    {
        private QueryExpr query;

        private Set<Filter> filters = Sets.newHashSet();

        private Set<Filter> queryFilters = Sets.newHashSet();

        private Set<Facet> facets = Sets.newHashSet();

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

        public Builder addFacet( final Facet facet )
        {
            this.facets.add( facet );
            return this;
        }

        public EntityQuery build()
        {
            return new EntityQuery( this );
        }
    }

}
