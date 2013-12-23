package com.enonic.wem.portal.postprocess;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class JavaElExpressionExecutorTest
{

    @Test
    public void testEvaluateJavaElExpr()
        throws Exception
    {
        final String expr = "${portal:createUrl('mycontent')}";
        final JavaElExpressionExecutor exprExecutor = new JavaElExpressionExecutor( true );

        final String result = exprExecutor.evaluateExpression( expr );

        assertEquals( "http://localhost/mycontent", result );
    }
}
