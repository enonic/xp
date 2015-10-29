package com.enonic.xp.portal.impl.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.impl.mapper.PortalResponseMapper;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.site.filter.FilterDescriptor;

public final class FilterExecutor
{
    private final static Logger LOG = LoggerFactory.getLogger( FilterExecutor.class );

    private static final String RESPONSE_FILTER_METHOD = "responseFilter";

    private final PortalScriptService scriptService;

    public FilterExecutor( final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    public PortalResponse executeResponseFilter( final FilterDescriptor filter, final PortalRequest request, final PortalResponse response )
    {
        final String filterName = filter.getName();
        final String filterJsPath = "/site/filters/" + filterName + ".js";
        final ResourceKey script = ResourceKey.from( filter.getApplication(), filterJsPath );
        final ScriptExports filterExports;
        try
        {
            filterExports = this.scriptService.execute( script );
        }
        catch ( ResourceNotFoundException e )
        {
            LOG.warn( "Filter execution failed: {}", e.getMessage() );
            throw e;
        }

        final boolean exists = filterExports.hasMethod( RESPONSE_FILTER_METHOD );
        if ( !exists )
        {
            throw new RenderException( "Missing exported function [{0}] in response filter [{1}]", RESPONSE_FILTER_METHOD, filterJsPath );
        }

        final ApplicationKey previousApp = request.getApplicationKey();
        // set application of the filter in the current context PortalRequest
        request.setApplicationKey( filter.getApplication() );

        PortalRequestAccessor.set( request );
        try
        {
            final PortalRequestMapper requestMapper = new PortalRequestMapper( request );
            final PortalResponseMapper responseMapper = new PortalResponseMapper( response );

            final ScriptValue result = filterExports.executeMethod( RESPONSE_FILTER_METHOD, requestMapper, responseMapper );
            return new PortalResponseSerializer( result ).serialize();
        }
        finally
        {
            PortalRequestAccessor.remove();
            request.setApplicationKey( previousApp );
        }
    }

}
