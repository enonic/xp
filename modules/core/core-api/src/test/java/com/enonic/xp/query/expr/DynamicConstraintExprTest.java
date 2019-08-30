package com.enonic.xp.query.expr;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicConstraintExprTest
{
    @Test
    public void testExpression()
    {
        final FunctionExpr func = new FunctionExpr( "name", Lists.newArrayList() );
        final DynamicConstraintExpr expr = new DynamicConstraintExpr( func );

        assertSame( func, expr.getFunction() );
        assertEquals( "name()", expr.toString() );
    }
}
