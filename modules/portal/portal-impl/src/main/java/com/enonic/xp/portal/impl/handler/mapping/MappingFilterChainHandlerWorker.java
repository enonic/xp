package com.enonic.xp.portal.impl.handler.mapping;

import java.util.List;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.filter.FilterScript;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

final class MappingFilterChainHandlerWorker
{
    private final PortalRequest request;

    private final WebResponse response;

    private final WebHandlerChain webHandlerChain;

    private final List<ControllerMappingDescriptor> filterDescriptors;

    ResourceService resourceService;

    FilterScriptFactory filterScriptFactory;

    MappingFilterChainHandlerWorker( final PortalRequest request, final WebResponse response, final WebHandlerChain webHandlerChain,
                                     final List<ControllerMappingDescriptor> filterDescriptors )
    {
        this.request = request;
        this.response = response;
        this.webHandlerChain = webHandlerChain;
        this.filterDescriptors = filterDescriptors;
    }

    public PortalResponse execute()
        throws Exception
    {
        final FilterChain filterChain = new FilterChain( filterDescriptors, webHandlerChain );
        return (PortalResponse) filterChain.handle( request, response );
    }

    private FilterScript getScript( final ControllerMappingDescriptor descriptor )
    {
        final Resource resource = this.resourceService.getResource( descriptor.getFilter() );
        if ( !resource.exists() )
        {
            throw WebException.notFound( String.format( "Filter [%s] not found", descriptor.getFilter() ) );
        }
        return this.filterScriptFactory.fromScript( resource.getKey() );
    }

    private final class FilterChain
        implements WebHandlerChain
    {
        private final List<ControllerMappingDescriptor> filters;

        private final WebHandlerChain originalChain;

        private int index;

        FilterChain( final List<ControllerMappingDescriptor> filters, final WebHandlerChain originalChain )
        {
            this.filters = filters;
            this.originalChain = originalChain;
        }

        @Override
        public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse )
            throws Exception
        {
            if ( index < filters.size() )
            {
                final ControllerMappingDescriptor nextFilter = filters.get( index++ );
                final Trace trace = Tracer.current();
                if ( trace != null )
                {
                    trace.put( "contentPath",
                               ( (PortalRequest) webRequest ).getContentPath() != null ? ( (PortalRequest) webRequest ).getContentPath().toString() : null );
                    trace.put( "type", "filter" );
                    trace.put( "filter", nextFilter.getFilter().toString() );
                }

                final FilterScript filterScript = getScript( nextFilter );
                return filterScript.execute( (PortalRequest) webRequest, webResponse, this );
            }
            else
            {
                return originalChain.handle( webRequest, webResponse );
            }
        }
    }
}
