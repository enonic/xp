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

final class MappingFilterHandlerWorker
{
    @FunctionalInterface
    interface ControllerInvoker
    {
        PortalResponse invoke( ControllerMappingDescriptor mapping )
            throws Exception;
    }

    private final PortalRequest request;

    private final WebResponse response;

    private final WebHandlerChain webHandlerChain;

    private final List<ControllerMappingDescriptor> mappingDescriptors;

    private final ControllerInvoker controllerInvoker;

    ResourceService resourceService;

    FilterScriptFactory filterScriptFactory;

    MappingFilterHandlerWorker( final PortalRequest request, final WebResponse response, final WebHandlerChain webHandlerChain,
                                final List<ControllerMappingDescriptor> mappingDescriptors, final ControllerInvoker controllerInvoker )
    {
        this.request = request;
        this.response = response;
        this.webHandlerChain = webHandlerChain;
        this.mappingDescriptors = mappingDescriptors;
        this.controllerInvoker = controllerInvoker;
    }

    public PortalResponse execute()
        throws Exception
    {
        return (PortalResponse) new FilterChain().handle( this.request, this.response );
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
        private int index;

        @Override
        public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse )
            throws Exception
        {
            if ( index >= mappingDescriptors.size() )
            {
                return webHandlerChain.handle( webRequest, webResponse );
            }

            final ControllerMappingDescriptor mapping = mappingDescriptors.get( index++ );
            final Trace trace = Tracer.current();
            if ( trace != null )
            {
                trace.put( "contentPath", request.getContentPath() != null ? request.getContentPath().toString() : null );
            }

            if ( mapping.isController() )
            {
                request.setApplicationKey( mapping.getApplication() );
                return controllerInvoker.invoke( mapping );
            }

            if ( trace != null )
            {
                trace.put( "type", "filter" );
                trace.put( "filter", mapping.getFilter().toString() );
            }

            return getScript( mapping ).execute( (PortalRequest) webRequest, webResponse, this );
        }
    }
}
