package com.enonic.xp.query.expr;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class NotExprTest
{
    @Test
    void testExpression()
    {
        final CompareExpr inner = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final NotExpr expr = new NotExpr( inner );

        assertSame( inner, expr.getExpression() );
        assertEquals( "NOT (a = 2.0)", expr.toString() );
    }
}
