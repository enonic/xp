package com.enonic.xp.portal.impl.error;

import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.impl.mapper.PortalErrorMapper;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

final class ErrorHandlerScriptImpl
    implements ErrorHandlerScript
{
    private final ScriptExports scriptExports;

    public ErrorHandlerScriptImpl( final ScriptExports scriptExports )
    {
        this.scriptExports = scriptExports;
    }

    @Override
    public PortalResponse execute( final PortalError portalError, final String handlerMethod )
    {
        if ( !canHandleError( handlerMethod ) )
        {
            return null;
        }

        PortalRequestAccessor.set( portalError.getRequest() );
        try
        {
            return Tracer.trace( "controllerScript", () -> doExecute( portalError, handlerMethod ) );
        }
        finally
        {
            PortalRequestAccessor.remove();
        }
    }

    private PortalResponse doExecute( final PortalError portalError, final String handlerMethod )
    {
        Tracer.withCurrent( this::addTraceInfo );

        final PortalErrorMapper portalErrorMapper = new PortalErrorMapper( portalError );
        final ScriptValue result = this.scriptExports.executeMethod( handlerMethod, portalErrorMapper );

        if ( ( result == null ) || !result.isObject() )
        {
            return null;
        }
        else
        {
            return new PortalResponseSerializer( result, portalError.getStatus() ).
                defaultPostProcess( false ).
                applyFilters( false ).
                serialize();
        }
    }

    private void addTraceInfo( final Trace trace )
    {
        trace.put( "script", this.scriptExports.getScript().toString() );
    }

    private boolean canHandleError( final String handlerMethod )
    {
        return scriptExports.hasMethod( handlerMethod );
    }

}
