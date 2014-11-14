package com.enonic.wem.repo;


import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.filter.Filters;
import com.enonic.wem.api.security.Principals;

public class NodeQuery
{
    private final NodePath parent;

    private final NodePath path;

    private final QueryExpr query;

    private final ImmutableSet<NodeId> ids;

    private final Filters postFilters;

    private final Filters queryFilters;

    private final ImmutableSet<AggregationQuery> aggregationQueries;

    private final ImmutableSet<OrderExpr> orderBys;

    private final Principals principals;

    private final int from;

    private final int size;

    private NodeQuery( final Builder builder )
    {
        this.query = builder.query;
        this.postFilters = builder.postFilters.build();
        this.queryFilters = builder.queryFilters.build();
        this.orderBys = query != null ? ImmutableSet.copyOf( query.getOrderSet() ) : ImmutableSet.<OrderExpr>of();
        this.size = builder.size;
        this.from = builder.from;
        this.aggregationQueries = ImmutableSet.copyOf( builder.aggregationQueries );
        this.ids = ImmutableSet.copyOf( builder.ids );
        this.parent = builder.parent;
        this.path = builder.path;
        this.principals = builder.principals;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public NodePath getPath()
    {
        return path;
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

    public Principals getPrincipals()
    {
        return principals;
    }

    public ImmutableSet<AggregationQuery> getAggregationQueries()
    {
        return aggregationQueries;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private NodePath parent;

        private NodePath path;

        private QueryExpr query;

        private Principals principals;

        private final Filters.Builder postFilters = Filters.create();

        private final Filters.Builder queryFilters = Filters.create();

        private final Set<AggregationQuery> aggregationQueries = Sets.newHashSet();

        private final Set<NodeId> ids = Sets.newHashSet();

        private int from = 0;

        private int size = 10;

        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder path( final NodePath path )
        {
            this.path = path;
            return this;
        }

        public Builder query( final QueryExpr query )
        {
            this.query = query;
            return this;
        }

        public Builder addId( final NodeId id )
        {
            this.ids.add( id );
            return this;
        }

        public Builder principals( final Principals principals )
        {
            this.principals = principals;
            return this;
        }

        public Builder addPostFilter( final Filter filter )
        {
            this.postFilters.add( filter );
            return this;
        }

        public Builder addQueryFilter( final Filter queryFilter )
        {
            this.queryFilters.add( queryFilter );
            return this;
        }


        public Builder addQueryFilters( final Filters queryFilters )
        {
            this.queryFilters.addAll( queryFilters.getSet() );
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

        public Builder addAggregationQueries( final Set<AggregationQuery> aggregationQueries )
        {
            this.aggregationQueries.addAll( aggregationQueries );
            return this;
        }

        public Builder addAggregationQuery( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
            return this;
        }

        public NodeQuery build()
        {
            return new NodeQuery( this );
        }
    }

}
