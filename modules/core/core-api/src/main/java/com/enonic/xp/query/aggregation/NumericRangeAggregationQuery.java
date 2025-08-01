package com.enonic.xp.query.aggregation;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class NumericRangeAggregationQuery
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

    public static final class Builder
        extends AbstractRangeAggregationQuery.Builder<Builder, NumericRange>
    {

        private Builder( final String name )
        {
            super( name );
        }

        public NumericRangeAggregationQuery build()
        {
            return new NumericRangeAggregationQuery( this );
        }
    }
}
