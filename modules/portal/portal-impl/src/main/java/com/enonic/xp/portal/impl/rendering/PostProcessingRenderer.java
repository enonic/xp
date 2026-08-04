package com.enonic.xp.portal.impl.rendering;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.processor.ProcessorChainResolver;
import com.enonic.xp.portal.impl.processor.ResponseProcessorExecutor;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;
import com.enonic.xp.trace.Traced;
import com.enonic.xp.trace.Tracer;

public abstract class PostProcessingRenderer<R>
    implements Renderer<R>
{
    protected final PostProcessor postProcessor;

    protected final ResponseProcessorExecutor processorExecutor;

    protected final ProcessorChainResolver processorChainResolver;

    public PostProcessingRenderer( final PostProcessor postProcessor, final ResponseProcessorExecutor processorExecutor,
                                   final ProcessorChainResolver processorChainResolver )
    {
        this.postProcessor = postProcessor;
        this.processorExecutor = processorExecutor;
        this.processorChainResolver = processorChainResolver;
    }

    @Override
    public PortalResponse render( final R component, final PortalRequest portalRequest )
    {
        PortalResponse portalResponse = doRender( component, portalRequest );
        portalResponse = this.postProcessor.processResponseInstructions( portalRequest, portalResponse );
        portalResponse = executeResponseProcessors( portalRequest, portalResponse );
        return isPageContributionsAllowed(component, portalRequest)
            ? this.postProcessor.processResponseContributions( portalRequest, portalResponse )
            : portalResponse;
    }

    protected abstract PortalResponse doRender( R component, PortalRequest portalRequest );

    protected boolean isPageContributionsAllowed( final R component, final PortalRequest portalRequest )
    {
        return true;
    }

    private PortalResponse executeResponseProcessors( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        if ( !portalResponse.applyFilters() )
        {
            return portalResponse;
        }

        final ResponseProcessorDescriptors filters = this.processorChainResolver.resolve( portalRequest );

        PortalResponse filterResponse = portalResponse;
        for ( ResponseProcessorDescriptor filter : filters )
        {
            filterResponse = executeProcessor( filter, portalRequest, filterResponse );

            if ( !filterResponse.applyFilters() )
            {
                return filterResponse;
            }
        }

        return filterResponse;
    }

    @Traced("renderFilter")
    private PortalResponse executeProcessor( final ResponseProcessorDescriptor filter, final PortalRequest portalRequest,
                                             final PortalResponse portalResponse )
    {
        Tracer.withCurrent( trace -> {
            trace.attribute( "app", filter.getApplication().toString() );
            trace.attribute( "name", filter.getName() );
            trace.attribute( "type", "filter" );
        } );
        return processorExecutor.execute( filter, portalRequest, portalResponse );
    }
}
