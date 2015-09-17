package com.enonic.xp.query.expr;

import org.junit.Assert;
import org.junit.Test;

public class FieldExprTest
{
    @Test
    public void testExpression()
    {
        final FieldExpr expr = FieldExpr.from( "name" );

        Assert.assertEquals( "name", expr.getFieldPath() );
        Assert.assertEquals( "name", expr.toString() );
    }
}
