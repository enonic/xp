package com.enonic.wem.portal.postprocess;

import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpResponse;
import com.enonic.wem.portal.rendering.RendererFactory;

final class PostProcessorHandler
{
    private JsContext context;

    private RendererFactory rendererFactory;

    public void execute()
    {
        final JsHttpResponse response = this.context.getResponse();
        if ( !response.isPostProcess() )
        {
            return;
        }

        final Object body = response.getBody();
        if ( !( body instanceof String ) )
        {
            return;
        }

        response.setBody( postProcess( (String) body ) );
    }

    private String postProcess( final String body )
    {
        return new PostProcessEvaluatorImpl( createInstructions() ).evaluate( body );
    }

    private PostProcessInstruction[] createInstructions()
    {
        return new PostProcessInstruction[]{new ComponentInstruction( this.rendererFactory, this.context )};
    }

    public PostProcessorHandler context( final JsContext context )
    {
        this.context = context;
        return this;
    }

    public PostProcessorHandler rendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
        return this;
    }
}
