package com.enonic.xp.query.expr;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DynamicConstraintExprTest
{
    @Test
    void testExpression()
    {
        final FunctionExpr func = new FunctionExpr( "name", new ArrayList<>() );
        final DynamicConstraintExpr expr = new DynamicConstraintExpr( func );

        assertSame( func, expr.getFunction() );
        assertEquals( "name()", expr.toString() );
    }
}
