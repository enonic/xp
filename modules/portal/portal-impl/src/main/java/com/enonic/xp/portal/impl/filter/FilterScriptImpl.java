package com.enonic.xp.portal.impl.filter;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.filter.FilterScript;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

final class FilterScriptImpl
    implements FilterScript
{
    private static final String FILTER_SCRIPT_METHOD = "filter";

    private final ScriptExports scriptExports;

    private final PortalScriptService scriptService;

    private final ResourceKey script;

    public FilterScriptImpl( final PortalScriptService scriptService, final ResourceKey script )
    {
        this.scriptExports = scriptService.execute( script );
        this.scriptService = scriptService;
        this.script = script;
    }

    @Override
    public PortalResponse execute( final PortalRequest request, final WebResponse response, final WebHandlerChain webHandlerChain )
    {
        PortalRequestAccessor.set( request );

        try
        {
            return Tracer.traceEx( "filterScript", () -> doExecute( request, response, webHandlerChain ) );
        }
        catch ( ResourceProblemException | WebException e )
        {
            throw e;
        }
        catch ( Throwable t )
        {
            final ResourceKey script = scriptExports.getScript();
            throw new WebException( HttpStatus.INTERNAL_SERVER_ERROR,
                                    "Error executing filter script: " + script.getApplicationKey() + ":" + script.getPath(), t );
        }
        finally
        {
            PortalRequestAccessor.remove();
        }
    }

    private void addTraceInfo( final Trace trace )
    {
        trace.put( "script", this.scriptExports.getScript().toString() );
    }

    private PortalResponse doExecute( final PortalRequest request, final WebResponse response, final WebHandlerChain webHandlerChain )
    {
        if ( !this.scriptExports.hasMethod( FILTER_SCRIPT_METHOD ) )
        {
            throw new WebException( HttpStatus.NOT_IMPLEMENTED,
                                    "Missing exported function '" + FILTER_SCRIPT_METHOD + "' in filter script: " + script.getUri() );
        }

        Tracer.withCurrent( this::addTraceInfo );
        final PortalRequestMapper requestMapper = new PortalRequestMapper( request );
        final FilterNextFunctionWrapper nextHandler =
            new FilterNextFunctionWrapper( webHandlerChain, request, response, scriptExports.getScript(), this::toScriptValue,
                                           this::toNativeObject );

        final ScriptValue result = this.scriptExports.executeMethod( FILTER_SCRIPT_METHOD, requestMapper, nextHandler );
        return new PortalResponseSerializer( result ).serialize();
    }

    private ScriptValue toScriptValue( final Object value )
    {
        return scriptService.toScriptValue( this.script, value );
    }

    private Object toNativeObject( final Object value )
    {
        return scriptService.toNativeObject( this.script, value );
    }
}
