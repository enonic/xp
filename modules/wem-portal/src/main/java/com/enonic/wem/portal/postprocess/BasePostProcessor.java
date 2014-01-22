package com.enonic.wem.portal.postprocess;

import com.google.inject.Inject;

import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpResponse;


abstract class BasePostProcessor
    implements PostProcessor
{
    @Inject
    protected ExpressionExecutor expressionExecutor;

    @Override
    public void processResponse( final JsContext context )
    {
        final JsHttpResponse response = context.getResponse();
        if ( !response.isPostProcess() )
        {
            return;
        }
        if ( !( response.getBody() instanceof String ) )
        {
            return;
        }
        final String responseBody = (String) response.getBody();
        final String processedResponse = doProcessStringResponse( responseBody );

        response.setBody( processedResponse );
    }

    private String doProcessStringResponse( final String responseBody )
    {
        try
        {
            return processStringResponse( responseBody );
        }
        catch ( Exception e )
        {
            throw new PostProcessException( "Failed to processStringResponse", e );
        }
    }

    protected abstract String processStringResponse( final String responseBody )
        throws Exception;

    public void setExpressionExecutor( final ExpressionExecutor expressionExecutor )
    {
        this.expressionExecutor = expressionExecutor;
    }
}
