package com.enonic.wem.api.query.expr;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

public class DynamicConstraintExprTest
{
    @Test
    public void testExpression()
    {
        final FunctionExpr func = new FunctionExpr( "name", Lists.newArrayList() );
        final DynamicConstraintExpr expr = new DynamicConstraintExpr( func );

        Assert.assertSame( func, expr.getFunction() );
        Assert.assertEquals( "name()", expr.toString() );
    }
}
