package com.enonic.xp.query.expr;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.OrderExpr;

public class DynamicOrderExprTest
{
    @Test
    public void testExpression()
    {
        final FunctionExpr func = new FunctionExpr( "name", Lists.newArrayList() );
        final DynamicOrderExpr expr = new DynamicOrderExpr( func, OrderExpr.Direction.DESC );

        Assert.assertSame( func, expr.getFunction() );
        Assert.assertEquals( OrderExpr.Direction.DESC, expr.getDirection() );
        Assert.assertEquals( "name() DESC", expr.toString() );
    }
}
