package com.enonic.xp.portal.impl.handler.mapping;

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
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

final class MappingFilterHandlerWorker
{
    private final PortalRequest request;

    private final WebResponse response;

    private final WebHandlerChain webHandlerChain;

    ResourceService resourceService;

    FilterScriptFactory filterScriptFactory;

    ControllerMappingDescriptor mappingDescriptor;

    MappingFilterHandlerWorker( final PortalRequest request, final WebResponse response, final WebHandlerChain webHandlerChain )
    {
        this.request = request;
        this.response = response;
        this.webHandlerChain = webHandlerChain;
    }

    public PortalResponse execute()
    {
        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", this.request.getContentPath().toString() );
            trace.put( "type", "filter" );
        }

        final FilterScript filterScript = getScript();
        return filterScript.execute( this.request, this.response, this.webHandlerChain );
    }

    private FilterScript getScript()
    {
        final Resource resource = this.resourceService.getResource( mappingDescriptor.getFilter() );
        if ( !resource.exists() )
        {
            throw WebException.notFound( String.format( "Filter [%s] not found", mappingDescriptor.getFilter() ) );
        }
        return this.filterScriptFactory.fromScript( resource.getKey() );
    }
}
