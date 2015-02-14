package com.enonic.xp.query.expr;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;

public class CompareExprTest
{
    @Test
    public void eq_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.eq( field, value );

        Assert.assertNotNull( expr );
        Assert.assertSame( field, expr.getField() );
        Assert.assertSame( value, expr.getFirstValue() );
        Assert.assertSame( value, expr.getValues().get( 0 ) );
        Assert.assertEquals( CompareExpr.Operator.EQ, expr.getOperator() );
        Assert.assertFalse( expr.getOperator().allowMultipleValues() );
        Assert.assertEquals( "a = 2.0", expr.toString() );
    }

    @Test
    public void neq_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.neq( field, value );

        Assert.assertNotNull( expr );
        Assert.assertSame( field, expr.getField() );
        Assert.assertSame( value, expr.getFirstValue() );
        Assert.assertSame( value, expr.getValues().get( 0 ) );
        Assert.assertEquals( CompareExpr.Operator.NEQ, expr.getOperator() );
        Assert.assertFalse( expr.getOperator().allowMultipleValues() );
        Assert.assertEquals( "a != 2.0", expr.toString() );
    }

    @Test
    public void gt_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.gt( field, value );

        Assert.assertNotNull( expr );
        Assert.assertSame( field, expr.getField() );
        Assert.assertSame( value, expr.getFirstValue() );
        Assert.assertSame( value, expr.getValues().get( 0 ) );
        Assert.assertEquals( CompareExpr.Operator.GT, expr.getOperator() );
        Assert.assertFalse( expr.getOperator().allowMultipleValues() );
        Assert.assertEquals( "a > 2.0", expr.toString() );
    }

    @Test
    public void gte_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.gte( field, value );

        Assert.assertNotNull( expr );
        Assert.assertSame( field, expr.getField() );
        Assert.assertSame( value, expr.getFirstValue() );
        Assert.assertSame( value, expr.getValues().get( 0 ) );
        Assert.assertEquals( CompareExpr.Operator.GTE, expr.getOperator() );
        Assert.assertFalse( expr.getOperator().allowMultipleValues() );
        Assert.assertEquals( "a >= 2.0", expr.toString() );
    }

    @Test
    public void lt_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.lt( field, value );

        Assert.assertNotNull( expr );
        Assert.assertSame( field, expr.getField() );
        Assert.assertSame( value, expr.getFirstValue() );
        Assert.assertSame( value, expr.getValues().get( 0 ) );
        Assert.assertEquals( CompareExpr.Operator.LT, expr.getOperator() );
        Assert.assertFalse( expr.getOperator().allowMultipleValues() );
        Assert.assertEquals( "a < 2.0", expr.toString() );
    }

    @Test
    public void lte_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.lte( field, value );

        Assert.assertNotNull( expr );
        Assert.assertSame( field, expr.getField() );
        Assert.assertSame( value, expr.getFirstValue() );
        Assert.assertSame( value, expr.getValues().get( 0 ) );
        Assert.assertEquals( CompareExpr.Operator.LTE, expr.getOperator() );
        Assert.assertFalse( expr.getOperator().allowMultipleValues() );
        Assert.assertEquals( "a <= 2.0", expr.toString() );
    }

    @Test
    public void like_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.string( "2" );
        final CompareExpr expr = CompareExpr.like( field, value );

        Assert.assertNotNull( expr );
        Assert.assertSame( field, expr.getField() );
        Assert.assertSame( value, expr.getFirstValue() );
        Assert.assertSame( value, expr.getValues().get( 0 ) );
        Assert.assertEquals( CompareExpr.Operator.LIKE, expr.getOperator() );
        Assert.assertFalse( expr.getOperator().allowMultipleValues() );
        Assert.assertEquals( "a LIKE '2'", expr.toString() );
    }

    @Test
    public void not_like_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.string( "2" );
        final CompareExpr expr = CompareExpr.notLike( field, value );

        Assert.assertNotNull( expr );
        Assert.assertSame( field, expr.getField() );
        Assert.assertSame( value, expr.getFirstValue() );
        Assert.assertSame( value, expr.getValues().get( 0 ) );
        Assert.assertEquals( CompareExpr.Operator.NOT_LIKE, expr.getOperator() );
        Assert.assertFalse( expr.getOperator().allowMultipleValues() );
        Assert.assertEquals( "a NOT LIKE '2'", expr.toString() );
    }

    @Test
    public void in_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final List<ValueExpr> values = Lists.newArrayList( ValueExpr.string( "1" ), ValueExpr.string( "2" ) );
        final CompareExpr expr = CompareExpr.in( field, values );

        Assert.assertNotNull( expr );
        Assert.assertSame( field, expr.getField() );
        Assert.assertSame( values.get( 0 ), expr.getFirstValue() );
        Assert.assertEquals( 2, expr.getValues().size() );
        Assert.assertEquals( CompareExpr.Operator.IN, expr.getOperator() );
        Assert.assertTrue( expr.getOperator().allowMultipleValues() );
        Assert.assertEquals( "a IN ('1', '2')", expr.toString() );
    }

    @Test
    public void not_in_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final List<ValueExpr> values = Lists.newArrayList( ValueExpr.string( "1" ), ValueExpr.string( "2" ) );
        final CompareExpr expr = CompareExpr.notIn( field, values );

        Assert.assertNotNull( expr );
        Assert.assertSame( field, expr.getField() );
        Assert.assertSame( values.get( 0 ), expr.getFirstValue() );
        Assert.assertEquals( 2, expr.getValues().size() );
        Assert.assertEquals( CompareExpr.Operator.NOT_IN, expr.getOperator() );
        Assert.assertTrue( expr.getOperator().allowMultipleValues() );
        Assert.assertEquals( "a NOT IN ('1', '2')", expr.toString() );
    }
}
