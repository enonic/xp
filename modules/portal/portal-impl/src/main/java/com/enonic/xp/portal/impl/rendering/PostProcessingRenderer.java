package com.enonic.xp.portal.impl.rendering;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.filter.FilterChainResolver;
import com.enonic.xp.portal.impl.filter.FilterExecutor;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.site.filter.FilterDescriptor;
import com.enonic.xp.site.filter.FilterDescriptors;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

public abstract class PostProcessingRenderer<R>
    implements Renderer<R>
{
    protected PostProcessor postProcessor;

    protected FilterExecutor filterExecutor;

    protected FilterChainResolver filterChainResolver;

    @Override
    public PortalResponse render( final R component, final PortalRequest portalRequest )
    {
        PortalResponse portalResponse = doRender( component, portalRequest );
        portalResponse = this.postProcessor.processResponseInstructions( portalRequest, portalResponse );
        portalResponse = executeResponseFilters( portalRequest, portalResponse );
        portalResponse = this.postProcessor.processResponseContributions( portalRequest, portalResponse );
        return portalResponse;
    }

    protected abstract PortalResponse doRender( final R component, final PortalRequest portalRequest );

    private PortalResponse executeResponseFilters( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        final FilterDescriptors filters = this.filterChainResolver.resolve( portalRequest );
        if ( !portalResponse.applyFilters() || filters.isEmpty() )
        {
            return portalResponse;
        }

        PortalResponse filterResponse = portalResponse;
        for ( FilterDescriptor filter : filters )
        {
            final PortalResponse filterPortalResponse = filterResponse;

            final Trace trace = Tracer.newTrace( "renderFilter" );
            if ( trace == null )
            {
                filterResponse = filterExecutor.executeResponseFilter( filter, portalRequest, filterPortalResponse );
            }
            else
            {
                trace.put( "app", filter.getApplication().toString() );
                trace.put( "name", filter.getName() );
                trace.put( "type", "filter" );
                filterResponse =
                    Tracer.trace( trace, () -> filterExecutor.executeResponseFilter( filter, portalRequest, filterPortalResponse ) );
            }

            if ( !filterResponse.applyFilters() )
            {
                break;
            }
        }

        return filterResponse;
    }
}
