package com.enonic.xp.query.expr;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompareExprTest
{
    @Test
    public void eq_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.eq( field, value );

        assertNotNull( expr );
        assertSame( field, expr.getField() );
        assertSame( value, expr.getFirstValue() );
        assertSame( value, expr.getValues().get( 0 ) );
        assertEquals( CompareExpr.Operator.EQ, expr.getOperator() );
        assertFalse( expr.getOperator().allowMultipleValues() );
        assertEquals( "a = 2.0", expr.toString() );
    }

    @Test
    public void neq_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.neq( field, value );

        assertNotNull( expr );
        assertSame( field, expr.getField() );
        assertSame( value, expr.getFirstValue() );
        assertSame( value, expr.getValues().get( 0 ) );
        assertEquals( CompareExpr.Operator.NEQ, expr.getOperator() );
        assertFalse( expr.getOperator().allowMultipleValues() );
        assertEquals( "a != 2.0", expr.toString() );
    }

    @Test
    public void gt_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.gt( field, value );

        assertNotNull( expr );
        assertSame( field, expr.getField() );
        assertSame( value, expr.getFirstValue() );
        assertSame( value, expr.getValues().get( 0 ) );
        assertEquals( CompareExpr.Operator.GT, expr.getOperator() );
        assertFalse( expr.getOperator().allowMultipleValues() );
        assertEquals( "a > 2.0", expr.toString() );
    }

    @Test
    public void gte_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.gte( field, value );

        assertNotNull( expr );
        assertSame( field, expr.getField() );
        assertSame( value, expr.getFirstValue() );
        assertSame( value, expr.getValues().get( 0 ) );
        assertEquals( CompareExpr.Operator.GTE, expr.getOperator() );
        assertFalse( expr.getOperator().allowMultipleValues() );
        assertEquals( "a >= 2.0", expr.toString() );
    }

    @Test
    public void lt_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.lt( field, value );

        assertNotNull( expr );
        assertSame( field, expr.getField() );
        assertSame( value, expr.getFirstValue() );
        assertSame( value, expr.getValues().get( 0 ) );
        assertEquals( CompareExpr.Operator.LT, expr.getOperator() );
        assertFalse( expr.getOperator().allowMultipleValues() );
        assertEquals( "a < 2.0", expr.toString() );
    }

    @Test
    public void lte_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.number( 2 );
        final CompareExpr expr = CompareExpr.lte( field, value );

        assertNotNull( expr );
        assertSame( field, expr.getField() );
        assertSame( value, expr.getFirstValue() );
        assertSame( value, expr.getValues().get( 0 ) );
        assertEquals( CompareExpr.Operator.LTE, expr.getOperator() );
        assertFalse( expr.getOperator().allowMultipleValues() );
        assertEquals( "a <= 2.0", expr.toString() );
    }

    @Test
    public void like_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.string( "2" );
        final CompareExpr expr = CompareExpr.like( field, value );

        assertNotNull( expr );
        assertSame( field, expr.getField() );
        assertSame( value, expr.getFirstValue() );
        assertSame( value, expr.getValues().get( 0 ) );
        assertEquals( CompareExpr.Operator.LIKE, expr.getOperator() );
        assertFalse( expr.getOperator().allowMultipleValues() );
        assertEquals( "a LIKE '2'", expr.toString() );
    }

    @Test
    public void not_like_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final ValueExpr value = ValueExpr.string( "2" );
        final CompareExpr expr = CompareExpr.notLike( field, value );

        assertNotNull( expr );
        assertSame( field, expr.getField() );
        assertSame( value, expr.getFirstValue() );
        assertSame( value, expr.getValues().get( 0 ) );
        assertEquals( CompareExpr.Operator.NOT_LIKE, expr.getOperator() );
        assertFalse( expr.getOperator().allowMultipleValues() );
        assertEquals( "a NOT LIKE '2'", expr.toString() );
    }

    @Test
    public void in_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final List<ValueExpr> values = List.of( ValueExpr.string( "1" ), ValueExpr.string( "2" ) );
        final CompareExpr expr = CompareExpr.in( field, values );

        assertNotNull( expr );
        assertSame( field, expr.getField() );
        assertSame( values.get( 0 ), expr.getFirstValue() );
        assertEquals( 2, expr.getValues().size() );
        assertEquals( CompareExpr.Operator.IN, expr.getOperator() );
        assertTrue( expr.getOperator().allowMultipleValues() );
        assertEquals( "a IN ('1', '2')", expr.toString() );
    }

    @Test
    public void not_in_compare()
    {
        final FieldExpr field = FieldExpr.from( "a" );
        final List<ValueExpr> values = List.of( ValueExpr.string( "1" ), ValueExpr.string( "2" ) );
        final CompareExpr expr = CompareExpr.notIn( field, values );

        assertNotNull( expr );
        assertSame( field, expr.getField() );
        assertSame( values.get( 0 ), expr.getFirstValue() );
        assertEquals( 2, expr.getValues().size() );
        assertEquals( CompareExpr.Operator.NOT_IN, expr.getOperator() );
        assertTrue( expr.getOperator().allowMultipleValues() );
        assertEquals( "a NOT IN ('1', '2')", expr.toString() );
    }
}
