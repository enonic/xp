package com.enonic.xp.query.expr;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class NotExprTest
{
    @Test
    public void testExpression()
    {
        final CompareExpr inner = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final NotExpr expr = new NotExpr( inner );

        assertSame( inner, expr.getExpression() );
        assertEquals( "NOT (a = 2.0)", expr.toString() );
    }
}
