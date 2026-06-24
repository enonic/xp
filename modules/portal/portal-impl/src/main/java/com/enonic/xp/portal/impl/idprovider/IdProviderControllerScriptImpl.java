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

final class IdProviderControllerScriptImpl
    implements IdProviderControllerScript
{
    private final ScriptExports scriptExports;

    IdProviderControllerScriptImpl( final ScriptExports scriptExports )
    {
        this.scriptExports = scriptExports;
    }

    @Override
    public boolean hasMethod( final String functionName )
    {
        return this.scriptExports.hasMethod( functionName );
    }

    @Override
    public PortalResponse execute( final String functionName, final PortalRequest request )
    {
        return execute( functionName, request, null );
    }

    @Override
    public PortalResponse execute( final String functionName, final PortalRequest request, final Object context )
    {
        return withRequest( request, () -> {
            if ( !this.scriptExports.hasMethod( functionName ) )
            {
                return new PortalResponseSerializer( null, HttpStatus.NOT_FOUND ).serialize();
            }
            final ScriptValue result = invoke( functionName, request, context );
            return ( result == null || !result.isObject() ) ? null : new PortalResponseSerializer( result ).serialize();
        } );
    }

    @Override
    public boolean executeBoolean( final String functionName, final PortalRequest request, final Object context )
    {
        return Boolean.TRUE.equals( withRequest( request, () -> {
            if ( !this.scriptExports.hasMethod( functionName ) )
            {
                return Boolean.FALSE;
            }
            final ScriptValue result = invoke( functionName, request, context );
            return result != null && result.isValue() ? result.getValue( Boolean.class ) : Boolean.FALSE;
        } ) );
    }

    private ScriptValue invoke( final String functionName, final PortalRequest portalRequest, final Object context )
    {
        final PortalRequestMapper requestMapper = new PortalRequestMapper( portalRequest );
        return context == null
            ? this.scriptExports.executeMethod( functionName, requestMapper )
            : this.scriptExports.executeMethod( functionName, requestMapper, context );
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
