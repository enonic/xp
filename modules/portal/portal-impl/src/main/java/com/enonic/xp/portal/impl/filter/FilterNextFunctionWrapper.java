package com.enonic.xp.portal.impl.filter;

import java.util.function.Function;

import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.mapper.PortalResponseMapper;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

public final class FilterNextFunctionWrapper
    implements Function<Value, Object>
{
    private final WebHandlerChain webHandlerChain;

    private final PortalRequest request;

    private final WebResponse response;

    private final ResourceKey script;

    private final Function<Object, ScriptValue> toScriptValue;

    private final Function<Object, Object> toNativeObject;

    private boolean functionWasCalled;

    public FilterNextFunctionWrapper( WebHandlerChain webHandlerChain, PortalRequest request, WebResponse response, ResourceKey script,
                                      Function<Object, ScriptValue> toScriptValue, Function<Object, Object> toNativeObject )
    {
        this.webHandlerChain = webHandlerChain;
        this.response = response;
        this.request = request;
        this.script = script;
        this.toScriptValue = toScriptValue;
        this.toNativeObject = toNativeObject;
    }

    @Override
    public Object apply( final Value scriptRequestObject )
    {
        if ( functionWasCalled )
        {
            throw scriptError( "Filter 'next' function was called multiple times", null );
        }
        functionWasCalled = true;

        ScriptValue scriptRequestParam = toScriptValue.apply( scriptRequestObject );
        try
        {
            final PortalRequest portalRequest = new PortalRequestSerializer( request, scriptRequestParam ).serialize();

            final WebResponse newResponse = webHandlerChain.handle( portalRequest, response );
            final PortalResponseMapper response = new PortalResponseMapper( (PortalResponse) newResponse );
            return toNativeObject.apply( response );
        }
        catch ( ResourceProblemException | WebException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw scriptError( "Error executing filter script: " + script.getApplicationKey() + ":" + script.getPath(), e );
        }
    }

    private ResourceProblemException scriptError( final String message, final Throwable cause )
    {
        return ResourceProblemException.create().
            resource( script ).
            lineNumber( findScriptLine() ).
            cause( cause ).
            message( message ).
            build();
    }

    private int findScriptLine()
    {
        try
        {
            throw new Exception();
        }
        catch ( Exception e )
        {
            StackTraceElement[] elements = null;
            if ( e instanceof PolyglotException )
            {
                PolyglotException ex = (PolyglotException) e;
                elements = ex.getStackTrace();
            }
            return elements != null && elements.length > 0 ? elements[0].getLineNumber() : 0;
        }
    }
}
