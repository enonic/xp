package com.enonic.xp.query.expr;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class DynamicOrderExprTest
{
    @Test
    void testExpression()
    {
        final FunctionExpr func = FunctionExpr.from( "name", new ArrayList<>() );
        final DynamicOrderExpr expr = new DynamicOrderExpr( func, OrderExpr.Direction.DESC );

        assertSame( func, expr.getFunction() );
        assertEquals( OrderExpr.Direction.DESC, expr.getDirection() );
        assertEquals( "name() DESC", expr.toString() );
    }

    @Test
    void testExpressionWithoutDirection()
    {
        final FunctionExpr func = FunctionExpr.from( "name", new ArrayList<>() );
        final DynamicOrderExpr expr = new DynamicOrderExpr( func, null );

        assertSame( func, expr.getFunction() );
        assertNull( expr.getDirection() );
        assertEquals( "name()", expr.toString() );
    }
}
