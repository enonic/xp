package com.enonic.xp.query.expr;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.query.expr.ValueExpr;

public class ValueExprTest
{
    @Test
    public void quoteForString()
    {
        quoteForString( "test", "'test'" );
        quoteForString( "te'st", "\"te'st\"" );
        quoteForString( "te\"st", "'te\"st'" );
    }

    private void quoteForString( final String value, final String expected )
    {
        final ValueExpr expr = ValueExpr.string( value );
        Assert.assertNotNull( expected );
        Assert.assertEquals( expected, expr.toString() );
    }

    @Test
    public void stringValue()
    {
        final ValueExpr expr = ValueExpr.string( "value" );

        Assert.assertNotNull( expr );
        Assert.assertEquals( "'value'", expr.toString() );
        Assert.assertEquals( ValueTypes.STRING, expr.getValue().getType() );
    }

    @Test
    public void numberValue()
    {
        final ValueExpr expr = ValueExpr.number( 33 );

        Assert.assertNotNull( expr );
        Assert.assertEquals( "33.0", expr.toString() );
        Assert.assertEquals( ValueTypes.DOUBLE, expr.getValue().getType() );
    }

    @Test
    public void dateTimeValue()
    {
        final ValueExpr expr = ValueExpr.instant( "2013-11-11T22:22:22.000Z" );

        Assert.assertNotNull( expr );
        Assert.assertEquals( "instant('2013-11-11T22:22:22Z')", expr.toString() );
        Assert.assertEquals( ValueTypes.INSTANT, expr.getValue().getType() );
    }

    @Test
    public void geoPointValue()
    {
        final ValueExpr expr = ValueExpr.geoPoint( "11,22" );

        Assert.assertNotNull( expr );
        Assert.assertEquals( "geoPoint('11.0,22.0')", expr.toString() );
        Assert.assertEquals( ValueTypes.GEO_POINT, expr.getValue().getType() );
    }
}
