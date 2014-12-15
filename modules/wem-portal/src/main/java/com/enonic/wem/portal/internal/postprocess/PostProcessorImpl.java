package com.enonic.wem.portal.internal.postprocess;

import java.util.List;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.PostProcessInjection;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

public final class PostProcessorImpl
    implements PostProcessor
{
    private List<PostProcessInstruction> instructions;

    private List<PostProcessInjection> injections;

    @Override
    public void processResponse( final PortalContext context )
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

    private void doPostProcess( final PortalContext context, final String body )
    {
        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.context = context;
        evaluator.input = body;
        evaluator.instructions = this.instructions;
        evaluator.injections = this.injections;

        context.getResponse().setBody( evaluator.evaluate() );
    }

    public void setInstructions( final List<PostProcessInstruction> instructions )
    {
        this.instructions = instructions;
    }

    public void setInjections( final List<PostProcessInjection> injections )
    {
        this.injections = injections;
    }
}
