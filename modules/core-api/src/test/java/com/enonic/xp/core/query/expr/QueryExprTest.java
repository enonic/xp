package com.enonic.xp.core.query.expr;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.core.query.expr.CompareExpr;
import com.enonic.xp.core.query.expr.FieldExpr;
import com.enonic.xp.core.query.expr.FieldOrderExpr;
import com.enonic.xp.core.query.expr.OrderExpr;
import com.enonic.xp.core.query.expr.QueryExpr;
import com.enonic.xp.core.query.expr.ValueExpr;

public class QueryExprTest
{
    @Test
    public void empty_query()
    {
        final QueryExpr expr = new QueryExpr( null, null );

        Assert.assertNull( expr.getConstraint() );
        Assert.assertEquals( 0, expr.getOrderSet().size() );
        Assert.assertEquals( "", expr.toString() );
    }

    @Test
    public void full_query()
    {
        final CompareExpr constraint = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final List<OrderExpr> orderList = Lists.newArrayList( new FieldOrderExpr( FieldExpr.from( "a" ), OrderExpr.Direction.DESC ) );
        final QueryExpr expr = new QueryExpr( constraint, orderList );

        Assert.assertSame( constraint, expr.getConstraint() );
        Assert.assertEquals( 1, expr.getOrderSet().size() );
        Assert.assertEquals( "a = 2.0 ORDER BY a DESC", expr.toString() );
    }

    @Test
    public void only_constraint_in_query()
    {
        final CompareExpr constraint = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final QueryExpr expr = new QueryExpr( constraint, null );

        Assert.assertSame( constraint, expr.getConstraint() );
        Assert.assertEquals( 0, expr.getOrderSet().size() );
        Assert.assertEquals( "a = 2.0", expr.toString() );
    }

    @Test
    public void only_order_in_query()
    {
        final List<OrderExpr> orderList = Lists.newArrayList( new FieldOrderExpr( FieldExpr.from( "a" ), OrderExpr.Direction.DESC ) );
        final QueryExpr expr = new QueryExpr( null, orderList );

        Assert.assertNull( expr.getConstraint() );
        Assert.assertEquals( 1, expr.getOrderSet().size() );
        Assert.assertEquals( "ORDER BY a DESC", expr.toString() );
    }
}
