package com.enonic.xp.query.expr;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;

@PublicApi
public final class ValueExpr
    implements Expression
{
    private final Value value;

    private ValueExpr( final Value value )
    {
        this.value = value;
    }

    public static ValueExpr string( final String value )
    {
        return new ValueExpr( ValueFactory.newString( value ) );
    }

    public static ValueExpr number( final Number value )
    {
        return new ValueExpr( ValueFactory.newDouble( value.doubleValue() ) );
    }

    public static ValueExpr instant( final String value )
    {
        return new ValueExpr( ValueFactory.newDateTime( ValueTypes.DATE_TIME.convert( value ) ) );
    }

    public static ValueExpr dateTime( final String value )
    {
        return new ValueExpr( ValueFactory.newDateTime( Instant.from( DateTimeFormatter.ISO_DATE_TIME.parse( value ) ) ) );
    }

    public static ValueExpr localDateTime( final String value )
    {
        return new ValueExpr( ValueFactory.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) ) );
    }

    public static ValueExpr time( final String value )
    {
        return new ValueExpr( ValueFactory.newLocalTime( ValueTypes.LOCAL_TIME.convert( value ) ) );
    }

    public static ValueExpr date( final String value )
    {
        return new ValueExpr( ValueFactory.newLocalDate( ValueTypes.LOCAL_DATE.convert( value ) ) );
    }

    public static ValueExpr geoPoint( final String value )
    {
        return new ValueExpr( ValueFactory.newGeoPoint( ValueTypes.GEO_POINT.convert( value ) ) );
    }

    public Value getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        final ValueType type = this.value.getType();
        if ( type.equals( ValueTypes.DOUBLE ) )
        {
            return this.value.asString();
        }

        if ( type.equals( ValueTypes.DATE_TIME ) )
        {
            return typecastFunction( "instant", this.value.asString() );
        }

        if ( type.equals( ValueTypes.GEO_POINT ) )
        {
            return typecastFunction( "geoPoint", this.value.asString() );
        }

        return quoteString( this.value.asString() );
    }

    private String typecastFunction( final String name, final String argument )
    {
        return name + "(" + quoteString( argument ) + ")";
    }

    private String quoteString( final String value )
    {
        if ( value.contains( "'" ) )
        {
            return "\"" + value + "\"";
        }
        else
        {
            return "'" + value + "'";
        }
    }
}
