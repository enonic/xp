package com.enonic.xp.query.expr;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

public class DynamicOrderExprTest
{
    @Test
    public void testExpression()
    {
        final FunctionExpr func = FunctionExpr.from( "name", Lists.newArrayList() );
        final DynamicOrderExpr expr = new DynamicOrderExpr( func, OrderExpr.Direction.DESC );

        Assert.assertSame( func, expr.getFunction() );
        Assert.assertEquals( OrderExpr.Direction.DESC, expr.getDirection() );
        Assert.assertEquals( "name() DESC", expr.toString() );
    }
}
