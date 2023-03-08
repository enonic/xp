package com.enonic.xp.aggregation;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class Aggregation
{
    private final String name;

    private final Aggregations subAggregations;

    protected Aggregation( final Builder<?> builder )
    {
        this.name = builder.name;
        this.subAggregations = Aggregations.from( builder.subAggregations.build() );
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

    public abstract static class Builder<T extends Builder>
    {
        public Builder( final String name )
        {
            this.name = name;
        }

        private final String name;

        private final Aggregations.Builder subAggregations = Aggregations.create();

        @SuppressWarnings("unchecked")
        public T addSubAggregation( final Aggregation aggregation )
        {
            this.subAggregations.add( aggregation );
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addAggregations( final Aggregations aggregations )
        {
            aggregations.stream().forEach( this.subAggregations::add );
            return (T) this;
        }

    }
}
