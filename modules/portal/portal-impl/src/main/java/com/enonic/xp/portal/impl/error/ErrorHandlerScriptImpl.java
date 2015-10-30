package com.enonic.xp.portal.impl.error;

import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.impl.mapper.PortalErrorMapper;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpStatus;

final class ErrorHandlerScriptImpl
    implements ErrorHandlerScript
{

    private static final String DEFAULT_HANDLER = "handleError";

    private static final String STATUS_HANDLER = "handle%d";

    private final ScriptExports scriptExports;

    public ErrorHandlerScriptImpl( final ScriptExports scriptExports )
    {
        this.scriptExports = scriptExports;
    }

    @Override
    public PortalResponse execute( final PortalError portalError )
    {
        if ( !canHandleError( portalError.getStatus() ) )
        {
            return null;
        }

        PortalRequestAccessor.set( portalError.getRequest() );
        try
        {
            return doExecute( portalError );
        }
        finally
        {
            PortalRequestAccessor.remove();
        }
    }

    private PortalResponse doExecute( final PortalError portalError )
    {
        String runMethod = handlerMethod( portalError.getStatus() );
        if ( !this.scriptExports.hasMethod( runMethod ) )
        {
            runMethod = DEFAULT_HANDLER;
            if ( !this.scriptExports.hasMethod( runMethod ) )
            {
                return null;
            }
        }

        final PortalErrorMapper portalErrorMapper = new PortalErrorMapper( portalError );
        final ScriptValue result = this.scriptExports.executeMethod( runMethod, portalErrorMapper );

        if ( ( result == null ) || !result.isObject() )
        {
            return null;
        }
        else
        {
            return new PortalResponseSerializer( result ).
                postProcess( false ).status( portalError.getStatus() ).applyFilters( false ).
                serialize();
        }
    }

    private boolean canHandleError( final HttpStatus status )
    {
        return scriptExports.hasMethod( handlerMethod( status ) ) || scriptExports.hasMethod( DEFAULT_HANDLER );
    }

    private String handlerMethod( final HttpStatus status )
    {
        return status == null ? DEFAULT_HANDLER : String.format( STATUS_HANDLER, status.value() );
    }

}
