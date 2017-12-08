package com.enonic.xp.query.aggregation;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

@Beta
public class NumericRangeAggregationQuery
    extends AbstractRangeAggregationQuery<NumericRange>
{

    private NumericRangeAggregationQuery( final Builder builder )
    {
        super( builder, builder.ranges );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            omitNullValues().
            add( "fieldName", getFieldName() ).
            add( "ranges", getRanges() ).
            toString();
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends AbstractRangeAggregationQuery.Builder<Builder, NumericRange>
    {

        public Builder( final String name )
        {
            super( name );
        }

        public NumericRangeAggregationQuery build()
        {
            return new NumericRangeAggregationQuery( this );
        }
    }
}
