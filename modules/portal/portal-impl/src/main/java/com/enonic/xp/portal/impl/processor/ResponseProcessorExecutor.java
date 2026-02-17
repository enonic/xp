package com.enonic.xp.portal.impl.processor;

import java.text.MessageFormat;

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
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;
import com.enonic.xp.trace.Tracer;

public final class ResponseProcessorExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( ResponseProcessorExecutor.class );

    private static final String RESPONSE_PROCESSOR_METHOD = "responseProcessor";

    private final PortalScriptService scriptService;

    public ResponseProcessorExecutor( final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    public PortalResponse execute( final ResponseProcessorDescriptor filter, final PortalRequest request, final PortalResponse response )
    {
        final ResourceKey script = ResourceKey.from( filter.getApplication(), "/cms/processors/" + filter.getName() + ".js" );
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

        final boolean exists = filterExports.hasMethod( RESPONSE_PROCESSOR_METHOD );
        if ( !exists )
        {
            throw new RenderException(
                MessageFormat.format( "Missing exported function [{0}] in response filter [{1}]", RESPONSE_PROCESSOR_METHOD,
                                      filterExports.getScript() ) );
        }

        final ApplicationKey previousApp = request.getApplicationKey();
        request.setApplicationKey( script.getApplicationKey() );
        PortalRequestAccessor.set( request );
        try
        {
            return Tracer.trace( "responseProcessorScript", trace -> trace.put( "script", filterExports.getScript().toString() ),
                                 () -> executeFilter( filterExports, request, response ) );
        }
        finally
        {
            PortalRequestAccessor.remove();
            request.setApplicationKey( previousApp );
        }
    }

    private PortalResponse executeFilter( final ScriptExports filterExports, final PortalRequest request, final PortalResponse response )
    {
        final PortalRequestMapper requestMapper = new PortalRequestMapper( request );
        final PortalResponseMapper responseMapper = new PortalResponseMapper( response );

        final ScriptValue result = filterExports.executeMethod( RESPONSE_PROCESSOR_METHOD, requestMapper, responseMapper );
        return new PortalResponseSerializer( result ).serialize();
    }
}
