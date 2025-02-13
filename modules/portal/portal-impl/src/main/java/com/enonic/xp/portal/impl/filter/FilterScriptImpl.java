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

    FilterScriptImpl( final PortalScriptService scriptService, final ResourceKey script )
    {
        this.scriptExports = scriptService.execute( script );
        this.scriptService = scriptService;
    }

    @Override
    public PortalResponse execute( final PortalRequest request, final WebResponse response, final WebHandlerChain webHandlerChain )
    {
        PortalRequestAccessor.set( request );
        try
        {
            return Tracer.trace( "filterScript", trace -> trace.put( "script", this.scriptExports.getScript().toString() ),
                                 () -> doExecute( request, response, webHandlerChain ) );
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

    private PortalResponse doExecute( final PortalRequest request, final WebResponse response, final WebHandlerChain webHandlerChain )
    {
        if ( !this.scriptExports.hasMethod( FILTER_SCRIPT_METHOD ) )
        {
            throw new WebException( HttpStatus.NOT_IMPLEMENTED,
                                    "Missing exported function '" + FILTER_SCRIPT_METHOD + "' in filter script: " +
                                        scriptExports.getScript() );
        }

        final PortalRequestMapper requestMapper = new PortalRequestMapper( request );
        final FilterNextFunctionWrapper nextHandler =
            new FilterNextFunctionWrapper( webHandlerChain, request, response, scriptExports.getScript(), scriptService );

        final ScriptValue result = this.scriptExports.executeMethod( FILTER_SCRIPT_METHOD, requestMapper, nextHandler );
        return new PortalResponseSerializer( result ).serialize();
    }
}
