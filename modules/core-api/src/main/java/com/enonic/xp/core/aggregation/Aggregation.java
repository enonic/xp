package com.enonic.xp.core.aggregation;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public abstract class Aggregation
{
    private final String name;

    private final Aggregations subAggregations;

    @SuppressWarnings("unchecked")
    protected Aggregation( final Builder builder )
    {
        this.name = builder.name;
        this.subAggregations = Aggregations.from( ImmutableSet.copyOf( builder.subAggregations ) );
    }

    public Aggregations getSubAggregations()
    {
        return subAggregations;
    }

    public String getName()
    {
        return name;
    }

    public static BucketAggregation.Builder bucketAggregation( final String name )
    {
        return new BucketAggregation.Builder( name );
    }

    public static abstract class Builder<T extends Builder>
    {
        public Builder( final String name )
        {
            this.name = name;
        }

        private final String name;

        private final Set<Aggregation> subAggregations = Sets.newHashSet();

        @SuppressWarnings("unchecked")
        public T addSubAggregation( final Aggregation aggregation )
        {
            this.subAggregations.add( aggregation );
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addAggregations( final Aggregations aggregations )
        {
            this.subAggregations.addAll( aggregations.getSet() );
            return (T) this;
        }

    }
}
