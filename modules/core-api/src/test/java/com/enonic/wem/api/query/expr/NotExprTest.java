package com.enonic.wem.api.query.expr;

import org.junit.Assert;
import org.junit.Test;

public class NotExprTest
{
    @Test
    public void testExpression()
    {
        final CompareExpr inner = CompareExpr.eq( FieldExpr.from( "a" ), ValueExpr.number( 2 ) );
        final NotExpr expr = new NotExpr( inner );

        Assert.assertSame( inner, expr.getExpression() );
        Assert.assertEquals( "NOT (a = 2.0)", expr.toString() );
    }
}
