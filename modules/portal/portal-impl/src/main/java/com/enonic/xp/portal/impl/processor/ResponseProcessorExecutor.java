package com.enonic.xp.portal.impl.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

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
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

public final class ResponseProcessorExecutor
{
    private final static Logger LOG = LoggerFactory.getLogger( ResponseProcessorExecutor.class );

    private static final String RESPONSE_PROCESSOR_METHOD = "responseProcessor";

    private final PortalScriptService scriptService;

    public ResponseProcessorExecutor( final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    public PortalResponse execute( final ResponseProcessorDescriptor filter, final PortalRequest request, final PortalResponse response )
    {
        final String filterName = filter.getName();
        final String filterJsPath = "/site/processors/" + filterName + ".js";
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

        final boolean exists = filterExports.hasMethod( RESPONSE_PROCESSOR_METHOD );
        if ( !exists )
        {
            throw new RenderException( "Missing exported function [{0}] in response filter [{1}]", RESPONSE_PROCESSOR_METHOD,
                                       filterJsPath );
        }

        final ApplicationKey previousApp = request.getApplicationKey();
        // set application of the filter in the current context PortalRequest
        request.setApplicationKey( filter.getApplication() );

        PortalRequestAccessor.set( request );
        try
        {
            return Tracer.trace( "controllerScript", () -> executeFilter( filterExports, request, response ) );
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
        final PortalResponseSerializer portalResponseSerializer = new PortalResponseSerializer( result );

        if ( unmodifiedByteSourceBody( response, result ) )
        {
            portalResponseSerializer.body( response.getBody() );
        }

        addTraceInfo( Tracer.current(), filterExports );

        return portalResponseSerializer.serialize();
    }

    private void addTraceInfo( final Trace trace, final ScriptExports scriptExports )
    {
        if ( trace != null )
        {
            trace.put( "script", scriptExports.getScript().toString() );
        }
    }

    private boolean unmodifiedByteSourceBody( final PortalResponse response, final ScriptValue scriptResult )
    {
        final boolean isByteSourceBody = response.getBody() instanceof ByteSource;
        if ( !isByteSourceBody || scriptResult == null )
        {
            return false;
        }

        final ScriptValue scriptBody = scriptResult.getMember( "body" );
        final String body = scriptBody != null && scriptBody.getValue() != null ? scriptBody.getValue().toString() : null;
        return response.getBody().toString().equals( body );
    }
}
