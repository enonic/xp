package com.enonic.xp.query.aggregation;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class BucketAggregationQuery
    extends AggregationQuery
{
    private final AggregationQueries subQueries;

    public BucketAggregationQuery( final Builder<?> builder )
    {
        super( builder );
        this.subQueries = AggregationQueries.fromCollection( builder.aggregationQueries.build() );
    }

    public AggregationQueries getSubQueries()
    {
        return subQueries;
    }

    public static class Builder<T>
        extends AggregationQuery.Builder<Builder>
    {
        public Builder( final String name )
        {
            super( name );
        }

        private ImmutableSet.Builder<AggregationQuery> aggregationQueries = ImmutableSet.builder();

        @SuppressWarnings("unchecked")
        public T addSubQuery( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
            return (T) this;
        }

        public T addSubQueries( final Iterable<AggregationQuery> aggregationQueries )
        {
            this.aggregationQueries.addAll( aggregationQueries );
            return (T) this;
        }
    }
}
