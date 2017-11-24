package com.enonic.xp.query.aggregation;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

import com.enonic.xp.util.GeoPoint;

@Beta
public class GeoDistanceAggregationQuery
    extends AbstractRangeAggregationQuery<DistanceRange>
{
    private final GeoPoint origin;

    private final String unit;

    private GeoDistanceAggregationQuery( final Builder builder )
    {
        super( builder, builder.ranges );
        this.origin = builder.origin;
        this.unit = builder.unit;
    }

    public GeoPoint getOrigin()
    {
        return origin;
    }

    public String getUnit()
    {
        return unit;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            omitNullValues().
            add( "fieldName", getFieldName() ).
            add( "ranges", getRanges() ).
            add( "origin", origin ).
            add( "unit", unit ).
            toString();
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static final class Builder
        extends AbstractRangeAggregationQuery.Builder<Builder, DistanceRange>
    {
        private GeoPoint origin;

        private String unit;

        private Builder( final String name )
        {
            super( name );
        }

        public Builder origin( GeoPoint origin )
        {
            this.origin = origin;
            return this;
        }

        public Builder unit( String unit )
        {
            this.unit = unit;
            return this;
        }

        public GeoDistanceAggregationQuery build()
        {
            return new GeoDistanceAggregationQuery( this );
        }
    }
}
