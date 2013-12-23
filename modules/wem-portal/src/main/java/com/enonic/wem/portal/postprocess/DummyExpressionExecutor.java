package com.enonic.wem.portal.postprocess;


public final class DummyExpressionExecutor
    implements ExpressionExecutor
{
    @Override
    public String evaluateExpression( final String expression )
        throws Exception
    {
        return "__" + expression.toUpperCase() + "__";
    }
}
