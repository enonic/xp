package com.enonic.xp.query.expr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class LogicalExprTest
{
    @Test
    void andExpression()
    {
        final CompareExpr left = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final CompareExpr right = CompareExpr.eq( FieldExpr.from( "b" ), ValueExpr.number( 2 ) );
        final LogicalExpr expr = LogicalExpr.and( left, right );

        assertSame( left, expr.getLeft() );
        assertSame( right, expr.getRight() );
        assertEquals( LogicalExpr.Operator.AND, expr.getOperator() );
        assertEquals( "(a = 2.0 AND b = 2.0)", expr.toString() );
    }

    @Test
    void orExpression()
    {
        final CompareExpr left = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final CompareExpr right = CompareExpr.eq( FieldExpr.from( "b" ), ValueExpr.number( 2 ) );
        final LogicalExpr expr = LogicalExpr.or( left, right );

        assertSame( left, expr.getLeft() );
        assertSame( right, expr.getRight() );
        assertEquals( LogicalExpr.Operator.OR, expr.getOperator() );
        assertEquals( "(a = 2.0 OR b = 2.0)", expr.toString() );
    }
}
