package com.enonic.xp.query.aggregation;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DateHistogramAggregationQuery
    extends AbstractHistogramAggregationQuery<String>
{
    private final String format;

    private DateHistogramAggregationQuery( final Builder builder )
    {
        super( builder, builder.interval );
        this.format = builder.format;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).omitNullValues().
            add( "name", getName() ).
            add( "fieldName", getFieldName() ).
            add( "format", format ).
            toString();
    }

    public String getFormat()
    {
        return format;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends AbstractHistogramAggregationQuery.Builder<Builder, String>
    {
        private String format;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder format( final String format )
        {
            this.format = format;
            return this;
        }

        public DateHistogramAggregationQuery build()
        {
            return new DateHistogramAggregationQuery( this );
        }
    }


}
