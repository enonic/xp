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
 * {@link PortalResponse}.
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
     * Executes the function and returns its result mapped to a plain Java value (a {@code Map} for an
     * object, a {@code List} for an array, or a scalar), rather than serializing it to a
     * {@link PortalResponse}. Used by the data hooks (e.g. {@code configure}). Returns {@code null} if
     * the function is absent or returns nothing.
     */
    public Object executeFunction( final String functionName, final PortalRequest request )
    {
        return withRequest( request, () -> {
            if ( !this.scriptExports.hasMethod( functionName ) )
            {
                return null;
            }
            final ScriptValue result = invoke( functionName, request );
            if ( result == null )
            {
                return null;
            }
            return result.isObject() ? result.getMap() : result.getValue();
        } );
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
