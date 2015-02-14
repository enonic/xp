package com.enonic.wem.api.query.expr;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

public class FunctionExprTest
{
    @Test
    public void testExpression()
    {
        final ValueExpr arg = ValueExpr.string( "arg1" );
        final FunctionExpr expr = new FunctionExpr( "name", Lists.newArrayList( arg ) );

        Assert.assertEquals( "name", expr.getName() );
        Assert.assertNotNull( expr.getArguments() );
        Assert.assertEquals( 1, expr.getArguments().size() );
        Assert.assertSame( arg, expr.getArguments().get( 0 ) );
        Assert.assertEquals( "name('arg1')", expr.toString() );
    }
}
