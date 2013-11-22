package com.enonic.wem.query.expr;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;

public final class ValueExpr
    implements Expression
{
    private final Value<?> value;

    private ValueExpr( final Value<?> value )
    {
        this.value = value;
    }

    public Value<?> getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        final ValueType type = this.value.getType();
        if ( type == ValueTypes.DOUBLE )
        {
            return this.value.asString();
        }

        if ( type == ValueTypes.DATE_TIME )
        {
            return typecastFunction( "dateTime", this.value.asString() );
        }

        if ( type == ValueTypes.GEO_POINT )
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

    public static ValueExpr string( final String value )
    {
        return new ValueExpr( new Value.String( value ) );
    }

    public static ValueExpr number( final Number value )
    {
        return new ValueExpr( new Value.Double( value.doubleValue() ) );
    }

    public static ValueExpr dateTime( final String value )
    {
        return new ValueExpr( new Value.DateTime( value ) );
    }

    public static ValueExpr geoPoint( final String value )
    {
        return new ValueExpr( new Value.GeoPoint( value ) );
    }
}
