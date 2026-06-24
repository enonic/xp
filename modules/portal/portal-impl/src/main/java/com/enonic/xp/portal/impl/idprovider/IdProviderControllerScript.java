package com.enonic.xp.portal.impl.idprovider;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpStatus;

/**
 * Executes an id provider controller script (idprovider.js) function, mapping the result to a
 * {@link PortalResponse} or a boolean.
 */
class IdProviderControllerScript
{
    private final ScriptExports scriptExports;

    IdProviderControllerScript( final ScriptExports scriptExports )
    {
        this.scriptExports = scriptExports;
    }

    public boolean hasMethod( final String functionName )
    {
        return this.scriptExports.hasMethod( functionName );
    }

    public PortalResponse execute( final String functionName, final PortalRequest request )
    {
        return withRequest( request, () -> {
            if ( !this.scriptExports.hasMethod( functionName ) )
            {
                return new PortalResponseSerializer( null, HttpStatus.NOT_FOUND ).serialize();
            }
            final ScriptValue result = invoke( functionName, request );
            return ( result == null || !result.isObject() ) ? null : new PortalResponseSerializer( result ).serialize();
        } );
    }

    /**
     * Executes the function and returns its boolean result, or {@code false} if it is missing or does
     * not return a boolean.
     */
    public boolean executeBoolean( final String functionName, final PortalRequest request )
    {
        return Boolean.TRUE.equals( withRequest( request, () -> {
            if ( !this.scriptExports.hasMethod( functionName ) )
            {
                return Boolean.FALSE;
            }
            final ScriptValue result = invoke( functionName, request );
            return result != null && result.isValue() ? result.getValue( Boolean.class ) : Boolean.FALSE;
        } ) );
    }

    private ScriptValue invoke( final String functionName, final PortalRequest portalRequest )
    {
        return this.scriptExports.executeMethod( functionName, new PortalRequestMapper( portalRequest ) );
    }

    private <T> T withRequest( final PortalRequest request, final java.util.function.Supplier<T> action )
    {
        final ApplicationKey previousApp = request.getApplicationKey();
        request.setApplicationKey( scriptExports.getScript().getApplicationKey() );
        PortalRequestAccessor.set( request );
        try
        {
            return action.get();
        }
        finally
        {
            PortalRequestAccessor.remove();
            request.setApplicationKey( previousApp );
        }
    }
}
