package com.enonic.xp.query.expr;

import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;

@PublicApi
public final class DslOrderExpr
    extends OrderExpr
{
    private final String field;

    private final String type;

    private final String unit;

    private final Double lat;

    private final Double lon;

    private DslOrderExpr( final PropertyTree expression, final Direction direction )
    {
        super( direction );
        this.field = expression.getString( "field" );
        this.type = expression.getString( "type" );
        this.unit = expression.getString( "unit" );

        final PropertySet location = expression.getSet( "location" );
        this.lat = location != null ? Objects.requireNonNull( location.getDouble( "lat" ) ) : null;
        this.lon = location != null ? Objects.requireNonNull( location.getDouble( "lon" ) ) : null;
    }

    public static DslOrderExpr from( final PropertyTree expression )
    {
        validate( expression );

        return new DslOrderExpr( expression,
                                 Optional.ofNullable( expression.getString( "direction" ) ).map( Direction::valueOf ).orElse( null ) );
    }

    private static void validate( final PropertyTree expression )
    {
        Preconditions.checkArgument( expression.hasProperty( "field" ), "field must be set" );
    }

    public String getField()
    {
        return field;
    }

    public String getType()
    {
        return type;
    }

    public String getUnit()
    {
        return unit;
    }

    public Double getLat()
    {
        return lat;
    }

    public Double getLon()
    {
        return lon;
    }

    @Override
    public String toString()
    {
        return getField() + ( getDirection() != null ? " " + getDirection().name() : "" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final DslOrderExpr orderExpr = (DslOrderExpr) o;
        return Objects.equals( field, orderExpr.field ) && Objects.equals( type, orderExpr.type ) &&
            Objects.equals( unit, orderExpr.unit ) && Objects.equals( lat, orderExpr.lat ) && Objects.equals( lon, orderExpr.lon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), field, type, unit, lat, lon );
    }
}
