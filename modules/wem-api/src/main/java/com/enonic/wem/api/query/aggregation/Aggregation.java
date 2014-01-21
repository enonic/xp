package com.enonic.wem.api.query.aggregation;

public abstract class Aggregation
{
    private final String name;

    public Aggregation( final Builder builder )
    {
        this.name = builder.name;
    }

    public String getName()
    {
        return name;
    }

    public static TermsAggregation.Builder terms()
    {
        return new TermsAggregation.Builder();
    }

    public static class Builder<T extends Builder>
    {
        private String name;

        public T name( final String name )
        {
            this.name = name;
            return (T) this;
        }

    }
}
