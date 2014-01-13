package com.enonic.wem.portal.postprocess;

import com.google.inject.Inject;

import com.enonic.wem.portal.controller.JsHttpResponse;


abstract class BasePostProcessor
    implements PostProcessor
{
    @Inject
    protected ExpressionExecutor expressionExecutor;

    @Override
    public void processResponse( final JsHttpResponse response )
        throws Exception
    {
        if ( !response.isPostProcess() )
        {
            return;
        }
        if ( !( response.getBody() instanceof String ) )
        {
            return;
        }
        final String responseBody = (String) response.getBody();
        final String processedResponse = processStringResponse( responseBody );
        response.setBody( processedResponse );
    }

    protected abstract String processStringResponse( final String responseBody )
        throws Exception;

    public void setExpressionExecutor( final ExpressionExecutor expressionExecutor )
    {
        this.expressionExecutor = expressionExecutor;
    }
}
