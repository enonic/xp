package com.enonic.wem.query;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.query.expr.QueryExpr;
import com.enonic.wem.query.queryfilter.QueryFilter;

public class EntityQuery
{
    private final QueryExpr query;

    private final ImmutableSet<QueryFilter> filters;

    private final ImmutableSet<QueryFilter> queryFilters;

    private final ImmutableSet<Facet> facets;

    public EntityQuery( final Builder builder )
    {
        this.query = builder.query;
        this.filters = ImmutableSet.copyOf( builder.filters );
        this.queryFilters = ImmutableSet.copyOf( builder.queryFilters );
        this.facets = ImmutableSet.copyOf( builder.facets );
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
    public ImmutableSet<QueryFilter> getFilters()
    {
        return filters;
    }

    // These are filters to be applied into query, and considered in facets also
    public ImmutableSet<QueryFilter> getQueryFilters()
    {
        return queryFilters;
    }

    public ImmutableSet<Facet> getFacets()
    {
        return facets;
    }

    public static class Builder
    {
        private QueryExpr query;

        private Set<QueryFilter> filters = Sets.newHashSet();

        private Set<QueryFilter> queryFilters = Sets.newHashSet();

        private Set<Facet> facets = Sets.newHashSet();

        public Builder query( final QueryExpr query )
        {
            this.query = query;
            return this;
        }

        public Builder addFilter( final QueryFilter filter )
        {
            this.filters.add( filter );
            return this;
        }

        public Builder addQueryFilter( final QueryFilter queryFilter )
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
