package com.enonic.xp.query.expr;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.ValueExpr;

public class LogicalExprTest
{
    @Test
    public void andExpression()
    {
        final CompareExpr left = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final CompareExpr right = CompareExpr.eq( FieldExpr.from( "b" ), ValueExpr.number( 2 ) );
        final LogicalExpr expr = LogicalExpr.and( left, right );

        Assert.assertSame( left, expr.getLeft() );
        Assert.assertSame( right, expr.getRight() );
        Assert.assertEquals( LogicalExpr.Operator.AND, expr.getOperator() );
        Assert.assertEquals( "(a = 2.0 AND b = 2.0)", expr.toString() );
    }

    @Test
    public void orExpression()
    {
        final CompareExpr left = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final CompareExpr right = CompareExpr.eq( FieldExpr.from( "b" ), ValueExpr.number( 2 ) );
        final LogicalExpr expr = LogicalExpr.or( left, right );

        Assert.assertSame( left, expr.getLeft() );
        Assert.assertSame( right, expr.getRight() );
        Assert.assertEquals( LogicalExpr.Operator.OR, expr.getOperator() );
        Assert.assertEquals( "(a = 2.0 OR b = 2.0)", expr.toString() );
    }
}
