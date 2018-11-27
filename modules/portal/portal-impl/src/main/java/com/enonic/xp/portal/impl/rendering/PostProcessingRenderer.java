package com.enonic.xp.portal.impl.rendering;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.processor.ProcessorChainResolver;
import com.enonic.xp.portal.impl.processor.ResponseProcessorExecutor;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

public abstract class PostProcessingRenderer<R>
    implements Renderer<R>
{
    protected PostProcessor postProcessor;

    protected ResponseProcessorExecutor processorExecutor;

    protected ProcessorChainResolver processorChainResolver;

    @Override
    public PortalResponse render( final R component, final PortalRequest portalRequest )
    {
        PortalResponse portalResponse = doRender( component, portalRequest );
        portalResponse = this.postProcessor.processResponseInstructions( portalRequest, portalResponse );
        portalResponse = executeResponseProcessors( portalRequest, portalResponse );
        portalResponse = this.postProcessor.processResponseContributions( portalRequest, portalResponse );
        return portalResponse;
    }

    protected abstract PortalResponse doRender( final R component, final PortalRequest portalRequest );

    private PortalResponse executeResponseProcessors( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        final ResponseProcessorDescriptors filters = this.processorChainResolver.resolve( portalRequest );
        if ( !portalResponse.applyFilters() || filters.isEmpty() )
        {
            return portalResponse;
        }

        PortalResponse filterResponse = portalResponse;
        for ( ResponseProcessorDescriptor filter : filters )
        {
            final PortalResponse filterPortalResponse = filterResponse;

            final Trace trace = Tracer.newTrace( "renderFilter" );
            if ( trace == null )
            {
                filterResponse = processorExecutor.execute( filter, portalRequest, filterPortalResponse );
            }
            else
            {
                trace.put( "app", filter.getApplication().toString() );
                trace.put( "name", filter.getName() );
                trace.put( "type", "filter" );
                filterResponse = Tracer.trace( trace, () -> processorExecutor.execute( filter, portalRequest, filterPortalResponse ) );
            }

            if ( !filterResponse.applyFilters() )
            {
                break;
            }
        }

        return filterResponse;
    }
}
