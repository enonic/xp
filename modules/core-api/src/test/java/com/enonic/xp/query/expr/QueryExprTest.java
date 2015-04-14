package com.enonic.xp.query.expr;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

public class QueryExprTest
{
    @Test
    public void empty_query()
    {
        final QueryExpr expr = new QueryExpr( null, null );

        Assert.assertNull( expr.getConstraint() );
        Assert.assertEquals( 0, expr.getOrderList().size() );
        Assert.assertEquals( "", expr.toString() );
    }

    @Test
    public void full_query()
    {
        final CompareExpr constraint = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final List<OrderExpr> orderList = Lists.newArrayList( new FieldOrderExpr( FieldExpr.from( "a" ), OrderExpr.Direction.DESC ) );
        final QueryExpr expr = new QueryExpr( constraint, orderList );

        Assert.assertSame( constraint, expr.getConstraint() );
        Assert.assertEquals( 1, expr.getOrderList().size() );
        Assert.assertEquals( "a = 2.0 ORDER BY a DESC", expr.toString() );
    }

    @Test
    public void only_constraint_in_query()
    {
        final CompareExpr constraint = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final QueryExpr expr = new QueryExpr( constraint, null );

        Assert.assertSame( constraint, expr.getConstraint() );
        Assert.assertEquals( 0, expr.getOrderList().size() );
        Assert.assertEquals( "a = 2.0", expr.toString() );
    }

    @Test
    public void only_order_in_query()
    {
        final List<OrderExpr> orderList = Lists.newArrayList( new FieldOrderExpr( FieldExpr.from( "a" ), OrderExpr.Direction.DESC ) );
        final QueryExpr expr1 = new QueryExpr( null, orderList );
        final QueryExpr expr2 = new QueryExpr( orderList );
        final QueryExpr expr3 = QueryExpr.from( null, orderList );

        Assert.assertNull( expr1.getConstraint() );
        Assert.assertEquals( 1, expr1.getOrderList().size() );
        Assert.assertEquals( "ORDER BY a DESC", expr1.toString() );

        Assert.assertNull( expr2.getConstraint() );
        Assert.assertEquals( 1, expr2.getOrderList().size() );
        Assert.assertEquals( "ORDER BY a DESC", expr2.toString() );

        Assert.assertNull( expr3.getConstraint() );
        Assert.assertEquals( 1, expr3.getOrderList().size() );
        Assert.assertEquals( "ORDER BY a DESC", expr3.toString() );
    }
}
