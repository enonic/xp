package com.enonic.xp.query.aggregation;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class MetricAggregationQuery
    extends AggregationQuery
{
    private final String fieldName;

    public MetricAggregationQuery( final Builder builder )
    {
        super( builder );
        this.fieldName = builder.fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public static class Builder<T extends Builder>
        extends AggregationQuery.Builder<Builder>
    {
        private String fieldName;

        public Builder( final String name )
        {
            super( name );
        }

        @SuppressWarnings("unchecked")
        public T fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return (T) this;
        }
    }

}
