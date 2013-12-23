package com.enonic.wem.portal.postprocess;


public interface ExpressionExecutor
{
    String evaluateExpression( String expression )
        throws Exception;
}
