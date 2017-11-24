package com.enonic.xp.query.aggregation.metric;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

import com.enonic.xp.query.aggregation.MetricAggregationQuery;

@Beta
public class MinAggregationQuery
    extends MetricAggregationQuery
{
    private MinAggregationQuery( final Builder builder )
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

        public MinAggregationQuery build()
        {
            return new MinAggregationQuery( this );
        }
    }
}
