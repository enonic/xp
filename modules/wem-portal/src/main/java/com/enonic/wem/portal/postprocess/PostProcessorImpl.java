package com.enonic.wem.portal.postprocess;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpResponse;
import com.enonic.wem.portal.postprocess.injection.PostProcessInjection;
import com.enonic.wem.portal.postprocess.instruction.PostProcessInstruction;

@Singleton
public final class PostProcessorImpl
    implements PostProcessor
{
    @Inject
    protected Set<PostProcessInstruction> instructions;

    // @Inject
    protected Set<PostProcessInjection> injections;

    @Override
    public void processResponse( final JsContext context )
    {
        final JsHttpResponse response = context.getResponse();
        if ( !response.isPostProcess() )
        {
            return;
        }

        final Object body = response.getBody();
        if ( !( body instanceof String ) )
        {
            return;
        }

        doPostProcess( context, (String) body );
    }

    private void doPostProcess( final JsContext context, final String body )
    {
        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.context = context;
        evaluator.input = body;
        evaluator.instructions = this.instructions;
        evaluator.injections = this.injections;

        context.getResponse().setBody( evaluator.evaluate() );
    }
}
