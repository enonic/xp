package com.enonic.xp.query.expr;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FunctionExprTest
{
    @Test
    public void testExpression()
    {
        final ValueExpr arg = ValueExpr.string( "arg1" );
        final FunctionExpr expr = new FunctionExpr( "name", Lists.newArrayList( arg ) );

        assertEquals( "name", expr.getName() );
        assertNotNull( expr.getArguments() );
        assertEquals( 1, expr.getArguments().size() );
        assertSame( arg, expr.getArguments().get( 0 ) );
        assertEquals( "name('arg1')", expr.toString() );
    }
}
