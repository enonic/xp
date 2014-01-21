package com.enonic.wem.api.query.aggregation;

public abstract class AggregationQuery
{
    private String name;

    AggregationQuery( final String name )
    {
        this.name = name;
    }

    public static TermsAggregationQuery.Builder newTermsAggregation( final String name )
    {
        return new TermsAggregationQuery.Builder( name );
    }


    public String getName()
    {
        return name;
    }

    public static class Builder<T extends Builder>
    {
        String name;

        public Builder( final String name )
        {
            this.name = name;
        }

        public T name( final String name )
        {
            this.name = name;
            return (T) this;
        }

    }

}
