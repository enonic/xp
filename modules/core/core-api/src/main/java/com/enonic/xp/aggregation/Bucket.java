package com.enonic.xp.aggregation;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class Bucket
{
    private final String key;

    private final long docCount;

    private final Aggregations subAggregations;

    @SuppressWarnings("unchecked")
    Bucket( final Builder builder )
    {
        this.key = builder.key;
        this.docCount = builder.docCount;
        this.subAggregations = builder.aggregations.build();
    }

    public String getKey()
    {
        return key;
    }

    public long getDocCount()
    {
        return docCount;
    }

    public Aggregations getSubAggregations()
    {
        return subAggregations;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder<T extends Builder>
    {
        private String key;

        private long docCount;

        final Aggregations.Builder aggregations = Aggregations.create();

        @SuppressWarnings("unchecked")
        public T key( final String key )
        {
            this.key = key;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T docCount( final long docCount )
        {
            this.docCount = docCount;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addAggregations( final Aggregations aggregations )
        {
            this.aggregations.addAll( aggregations );
            return (T) this;
        }

        public Bucket build()
        {
            return new Bucket( this );
        }
    }
}
