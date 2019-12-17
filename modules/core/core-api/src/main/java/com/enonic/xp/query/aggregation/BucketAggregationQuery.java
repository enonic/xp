package com.enonic.xp.query.aggregation;

import java.util.HashSet;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.Iterables;

@Beta
public abstract class BucketAggregationQuery
    extends AggregationQuery
{
    private final AggregationQueries subQueries;

    public BucketAggregationQuery( final Builder builder )
    {
        super( builder );
        this.subQueries = AggregationQueries.fromCollection( builder.aggregationQueries );
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

        private Set<AggregationQuery> aggregationQueries = new HashSet<>();

        @SuppressWarnings("unchecked")
        public T addSubQuery( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
            return (T) this;
        }

        public T addSubQueries( final Iterable<AggregationQuery> aggregationQueries )
        {
            Iterables.addAll( this.aggregationQueries, aggregationQueries );
            return (T) this;
        }
    }
}
