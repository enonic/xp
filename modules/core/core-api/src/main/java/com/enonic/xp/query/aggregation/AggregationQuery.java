package com.enonic.xp.query.aggregation;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class AggregationQuery
{
    private String name;

    @SuppressWarnings("unchecked")
    AggregationQuery( final Builder builder )
    {
        this.name = builder.name;
    }

    public String getName()
    {
        return name;
    }

    public static class Builder<T extends Builder>
    {
        private String name;

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


    }

}
