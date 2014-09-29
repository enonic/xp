package com.enonic.wem.portal.internal.postprocess;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.postprocess.injection.PostProcessInjection;
import com.enonic.wem.portal.internal.postprocess.instruction.PostProcessInstruction;

@Singleton
public final class PostProcessorImpl
    implements PostProcessor
{
    private Set<PostProcessInstruction> instructions;

    private Set<PostProcessInjection> injections;

    @Override
    public void processResponse( final JsContext context )
    {
        final PortalResponse response = context.getResponse();
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

    @Inject
    public void setInstructions( final Set<PostProcessInstruction> instructions )
    {
        this.instructions = instructions;
    }

    @Inject
    public void setInjections( final Set<PostProcessInjection> injections )
    {
        this.injections = injections;
    }
}
