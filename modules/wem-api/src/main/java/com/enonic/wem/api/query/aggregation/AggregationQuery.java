package com.enonic.wem.api.query.aggregation;

import java.util.Set;

import com.google.common.collect.Sets;

public abstract class AggregationQuery
{
    private String name;

    @SuppressWarnings("unchecked")
    AggregationQuery( final Builder builder )
    {
        this.name = builder.name;
        this.subQueries = AggregationQueries.fromCollection( builder.aggregationQueries );
    }

    public String getName()
    {
        return name;
    }

    private final AggregationQueries subQueries;

    public AggregationQueries getSubQueries()
    {
        return subQueries;
    }

    public static class Builder<T extends Builder>
    {
        private String name;

        private Set<AggregationQuery> aggregationQueries = Sets.newHashSet();

        public Builder( final String name )
        {
            this.name = name;
        }

        @SuppressWarnings("unchecked")
        public T name( final String name )
        {
            this.name = name;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T addSubQuery( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
            return (T) this;
        }

    }

}
