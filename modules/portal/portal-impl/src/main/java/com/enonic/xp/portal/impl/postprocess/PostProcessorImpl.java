package com.enonic.xp.portal.impl.postprocess;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.PostProcessInjection;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.web.HttpMethod;

@Component
public final class PostProcessorImpl
    implements PostProcessor
{
    private static final ImmutableList<MediaType> HTML_CONTENT_TYPES =
        ImmutableList.of( MediaType.create( "text", "html" ), MediaType.create( "application", "xhtml+xml" ) );

    private static final ImmutableSet<HttpMethod> METHODS_ALLOWED_TO_PROCESS = Sets.immutableEnumSet( HttpMethod.GET, HttpMethod.POST );

    private final List<PostProcessInstruction> instructions = new CopyOnWriteArrayList<>();

    private final List<PostProcessInjection> injections = new CopyOnWriteArrayList<>();

    @Override
    public PortalResponse processResponse( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        if ( skipPostprocess( portalRequest, portalResponse ) )
        {
            return portalResponse;
        }

        return postProcessEvaluator( portalRequest, portalResponse ).evaluate();
    }

    @Override
    public PortalResponse processResponseInstructions( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        if ( skipPostprocess( portalRequest, portalResponse ) )
        {
            return portalResponse;
        }

        return postProcessEvaluator( portalRequest, portalResponse ).evaluateInstructions();
    }

    @Override
    public PortalResponse processResponseContributions( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        if ( skipPostprocess( portalRequest, portalResponse ) )
        {
            return portalResponse;
        }

        return postProcessEvaluator( portalRequest, portalResponse ).evaluateContributions();
    }

    private boolean skipPostprocess( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        return !portalResponse.isPostProcess() || !( portalResponse.getBody() instanceof String ) ||
            !METHODS_ALLOWED_TO_PROCESS.contains( portalRequest.getMethod() ) || !isHtmlResponse( portalResponse );
    }

    private boolean isHtmlResponse( final PortalResponse portalResponse )
    {
        final MediaType contentType = portalResponse.getContentType();
        return contentType != null && HTML_CONTENT_TYPES.stream().anyMatch( contentType::is );
    }

    private PostProcessEvaluator postProcessEvaluator( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.portalRequest = portalRequest;
        evaluator.portalResponse = portalResponse;
        evaluator.input = (String) portalResponse.getBody();
        evaluator.instructions = this.instructions;
        evaluator.injections = this.injections;
        return evaluator;
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

