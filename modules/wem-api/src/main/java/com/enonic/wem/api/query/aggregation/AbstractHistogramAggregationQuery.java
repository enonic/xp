package com.enonic.wem.api.query.aggregation;

public abstract class AbstractHistogramAggregationQuery<T extends Interval>
    extends AggregationQuery
{
    private final String fieldName;

    private final T interval;

    AbstractHistogramAggregationQuery( final Builder builder, final T interval )
    {
        super( builder.name );
        this.fieldName = builder.fieldName;
        this.interval = interval;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public T getInterval()
    {
        return interval;
    }

    public abstract static class Builder<B extends Builder, T extends Interval>
        extends AggregationQuery.Builder
    {
        private String fieldName;

        T interval;

        public Builder( final String name )
        {
            super( name );
        }

        @SuppressWarnings("unchecked")
        public B interval( final T interval )
        {
            this.interval = interval;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return (B) this;
        }

    }


}
