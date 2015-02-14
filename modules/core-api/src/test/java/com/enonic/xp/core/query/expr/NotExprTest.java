package com.enonic.xp.core.query.expr;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.core.query.expr.CompareExpr;
import com.enonic.xp.core.query.expr.FieldExpr;
import com.enonic.xp.core.query.expr.NotExpr;
import com.enonic.xp.core.query.expr.ValueExpr;

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
