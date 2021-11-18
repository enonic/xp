package com.enonic.xp.portal.impl.filter;

import java.util.function.Function;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.mapper.PortalResponseMapper;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

public final class FilterNextFunctionWrapper
    implements Function<Object, Object>
{
    private final WebHandlerChain webHandlerChain;

    private final PortalRequest request;

    private final WebResponse response;

    private final ResourceKey script;

    private final PortalScriptService scriptService;

    private boolean functionWasCalled;

    public FilterNextFunctionWrapper( WebHandlerChain webHandlerChain, PortalRequest request, WebResponse response, ResourceKey script,
                                      final PortalScriptService scriptService )
    {
        this.webHandlerChain = webHandlerChain;
        this.response = response;
        this.request = request;
        this.script = script;
        this.scriptService = scriptService;
    }

    @Override
    public Object apply( final Object scriptRequestObject )
    {
        if ( functionWasCalled )
        {
            throw scriptError( "Filter 'next' function was called multiple times", null );
        }
        functionWasCalled = true;

        ScriptValue scriptRequestParam = scriptService.toScriptValue( this.script, scriptRequestObject );
        try
        {
            final PortalRequest portalRequest = new PortalRequestSerializer( request, scriptRequestParam ).serialize();

            final WebResponse newResponse = webHandlerChain.handle( portalRequest, response );
            final PortalResponseMapper response = new PortalResponseMapper( (PortalResponse) newResponse );
            return scriptService.toNativeObject( this.script, response );
        }
        catch ( ResourceProblemException | WebException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw scriptError( "Error executing filter script: " + script, e );
        }
    }

    private ResourceProblemException scriptError( final String message, final Throwable cause )
    {
        return ResourceProblemException.create().
            resource( script ).
            cause( cause ).
            message( message ).
            build();
    }
}
