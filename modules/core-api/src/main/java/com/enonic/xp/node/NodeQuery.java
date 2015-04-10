package com.enonic.xp.node;


import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.security.Principals;

@Beta
public class NodeQuery
{
    private final NodePath parent;

    private final NodePath path;

    private final QueryExpr query;

    private final Filters postFilters;

    private final Filters queryFilters;

    private final AggregationQueries aggregationQueries;

    private final ImmutableList<OrderExpr> orderBys;

    private final Principals principals;

    private final int from;

    private final int size;

    private final boolean countOnly;

    private final boolean resolveHasChild;

    private NodeQuery( final Builder builder )
    {
        this.query = builder.query;
        this.postFilters = builder.postFilters.build();
        this.queryFilters = builder.queryFilters.build();
        this.orderBys = query != null ? ImmutableList.copyOf( query.getOrderList() ) : ImmutableList.<OrderExpr>of();
        this.size = builder.size;
        this.from = builder.from;
        this.aggregationQueries = AggregationQueries.fromCollection( ImmutableSet.copyOf( builder.aggregationQueries ) );
        this.parent = builder.parent;
        this.path = builder.path;
        this.principals = builder.principals;
        this.countOnly = builder.countOnly;
        resolveHasChild = builder.resolveHasChild;
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

    public Principals getPrincipals()
    {
        return principals;
    }

    public AggregationQueries getAggregationQueries()
    {
        return aggregationQueries;
    }

    public boolean isCountOnly()
    {
        return countOnly;
    }

    public boolean resolveHasChild()
    {
        return resolveHasChild;
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

        private int from = 0;

        private int size = 10;

        private boolean countOnly = false;

        private boolean resolveHasChild = false;

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

        public Builder countOnly( final boolean countOnly )
        {
            this.countOnly = countOnly;
            return this;
        }

        public Builder addAggregationQueries( final AggregationQueries aggregationQueries )
        {
            this.aggregationQueries.addAll( aggregationQueries.getSet() );
            return this;
        }

        public Builder addAggregationQuery( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
            return this;
        }

        public Builder resolveHasChild( boolean resolveHasChild )
        {
            this.resolveHasChild = resolveHasChild;
            return this;
        }

        public NodeQuery build()
        {
            return new NodeQuery( this );
        }
    }

}
