package com.enonic.wem.portal.postprocess;

import com.google.inject.Inject;

import com.enonic.wem.portal.controller.JsHttpResponse;


abstract class BasePostProcessor
    implements PostProcessor
{
    protected int expressionCounter; // for testing

    @Inject
    protected ExpressionExecutor expressionExecutor;

    @Override
    public void processResponse( final JsHttpResponse response )
        throws Exception
    {
        if ( !( response.getBody() instanceof String ) )
        {
            return;
        }
        final String responseBody = (String) response.getBody();
        expressionCounter = 0;
        final String processedResponse = processStringResponse( responseBody );
        response.setBody( processedResponse );
    }

    protected abstract String processStringResponse( final String responseBody )
        throws Exception;

    protected int getExpressionCounter()
    {
        return expressionCounter;
    }
}
