package com.enonic.xp.portal.impl.error;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.impl.mapper.PortalErrorMapper;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.trace.Tracer;

final class ErrorHandlerScriptImpl
    implements ErrorHandlerScript
{
    private final ScriptExports scriptExports;

    ErrorHandlerScriptImpl( final ScriptExports scriptExports )
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

        final PortalRequest request = portalError.getRequest();
        final ApplicationKey previousApp = request.getApplicationKey();
        request.setApplicationKey( scriptExports.getScript().getApplicationKey() );
        PortalRequestAccessor.set( portalError.getRequest() );
        try
        {
            return Tracer.trace( "errorScript", trace -> trace.put( "script", this.scriptExports.getScript().toString() ),
                                 () -> doExecute( portalError, handlerMethod ) );
        }
        finally
        {
            PortalRequestAccessor.remove();
            request.setApplicationKey( previousApp );
        }
    }

    private PortalResponse doExecute( final PortalError portalError, final String handlerMethod )
    {
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


    private boolean canHandleError( final String handlerMethod )
    {
        return scriptExports.hasMethod( handlerMethod );
    }

}
