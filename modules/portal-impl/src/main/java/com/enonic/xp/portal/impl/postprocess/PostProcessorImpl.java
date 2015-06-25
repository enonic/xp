package com.enonic.xp.portal.impl.postprocess;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Lists;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.PostProcessInjection;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;
import com.enonic.xp.portal.postprocess.PostProcessor;

@Component
public final class PostProcessorImpl
    implements PostProcessor
{
    private final List<PostProcessInstruction> instructions;

    private final List<PostProcessInjection> injections;

    public PostProcessorImpl()
    {
        this.instructions = Lists.newCopyOnWriteArrayList();
        this.injections = Lists.newCopyOnWriteArrayList();
    }

    @Override
    public PortalResponse processResponse( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        if ( !portalResponse.isPostProcess() || !"GET".equals( portalRequest.getMethod() ) )
        {
            return portalResponse;
        }

        final Object body = portalResponse.getBody();
        if ( !( body instanceof String ) )
        {
            return portalResponse;
        }

        return doPostProcess( portalRequest, portalResponse );
    }

    private PortalResponse doPostProcess( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.portalRequest = portalRequest;
        evaluator.portalResponse = portalResponse;
        evaluator.input = (String) portalResponse.getBody();
        evaluator.instructions = this.instructions;
        evaluator.injections = this.injections;

        return evaluator.evaluate();
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addInstruction( final PostProcessInstruction value )
    {
        this.instructions.add( value );
    }

    public void removeInstruction( final PostProcessInstruction value )
    {
        this.instructions.remove( value );
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addInjection( final PostProcessInjection value )
    {
        this.injections.add( value );
    }

    public void removeInjection( final PostProcessInjection value )
    {
        this.injections.remove( value );
    }
}

