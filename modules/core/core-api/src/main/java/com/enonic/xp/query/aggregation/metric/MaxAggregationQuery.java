package com.enonic.xp.query.aggregation.metric;


import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.query.aggregation.MetricAggregationQuery;

@PublicApi
public class MaxAggregationQuery
    extends MetricAggregationQuery
{
    private MaxAggregationQuery( final Builder builder )
    {
        super( builder );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).omitNullValues().
            add( "name", getName() ).
            add( "fieldName", getFieldName() ).
            toString();
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends MetricAggregationQuery.Builder<Builder>
    {
        public Builder( final String name )
        {
            super( name );
        }

        public MaxAggregationQuery build()
        {
            return new MaxAggregationQuery( this );
        }
    }
}