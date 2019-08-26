package com.enonic.xp.query.expr;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

public class DynamicOrderExprTest
{
    @Test
    public void testExpression()
    {
        final FunctionExpr func = FunctionExpr.from( "name", Lists.newArrayList() );
        final DynamicOrderExpr expr = new DynamicOrderExpr( func, OrderExpr.Direction.DESC );

        assertSame( func, expr.getFunction() );
        assertEquals( OrderExpr.Direction.DESC, expr.getDirection() );
        assertEquals( "name() DESC", expr.toString() );
    }
}
