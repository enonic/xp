package com.enonic.xp.core.query.expr;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.core.query.expr.FieldExpr;
import com.enonic.xp.core.query.expr.FieldOrderExpr;
import com.enonic.xp.core.query.expr.OrderExpr;

public class FieldOrderExprTest
{
    @Test
    public void testExpression()
    {
        final FieldExpr field = FieldExpr.from( "name" );
        final FieldOrderExpr expr = new FieldOrderExpr( field, OrderExpr.Direction.DESC );

        Assert.assertSame( field, expr.getField() );
        Assert.assertEquals( OrderExpr.Direction.DESC, expr.getDirection() );
        Assert.assertEquals( "name DESC", expr.toString() );
    }
}
