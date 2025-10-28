package com.enonic.xp.query.expr;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValueExprTest
{
    @Test
    void quoteForString()
    {
        quoteForString( "test", "'test'" );
        quoteForString( "te'st", "\"te'st\"" );
        quoteForString( "te\"st", "'te\"st'" );
    }

    private void quoteForString( final String value, final String expected )
    {
        final ValueExpr expr = ValueExpr.string( value );
        assertNotNull( expected );
        assertEquals( expected, expr.toString() );
    }

    @Test
    void stringValue()
    {
        final ValueExpr expr = ValueExpr.string( "value" );

        assertNotNull( expr );
        assertEquals( "'value'", expr.toString() );
        assertEquals( ValueTypes.STRING, expr.getValue().getType() );
    }

    @Test
    void numberValue()
    {
        final ValueExpr expr = ValueExpr.number( 33 );

        assertNotNull( expr );
        assertEquals( "33.0", expr.toString() );
        assertEquals( ValueTypes.DOUBLE, expr.getValue().getType() );
    }

    @Test
    void dateTimeValue()
    {
        final ValueExpr expr = ValueExpr.instant( "2013-11-11T22:22:22.000Z" );

        assertNotNull( expr );
        assertEquals( "instant('2013-11-11T22:22:22Z')", expr.toString() );
        assertEquals( ValueTypes.DATE_TIME, expr.getValue().getType() );
    }

    @Test
    void geoPointValue()
    {
        final ValueExpr expr = ValueExpr.geoPoint( "11,22" );

        assertNotNull( expr );
        assertEquals( "geoPoint('11.0,22.0')", expr.toString() );
        assertEquals( ValueTypes.GEO_POINT, expr.getValue().getType() );
    }
}
