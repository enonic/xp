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
        final ApplicationKey previousApp = request.getApplicationKey();
        request.setApplicationKey( scriptExports.getScript().getApplicationKey() );
        PortalRequestAccessor.set( request );
        try
        {
            return doExecute( functionName, request );
        }
        finally
        {
            PortalRequestAccessor.remove();
            request.setApplicationKey( previousApp );
        }
    }

    private PortalResponse doExecute( final String functionName, final PortalRequest portalRequest )
    {
        final boolean exists = this.scriptExports.hasMethod( functionName );
        if ( !exists )
        {
            return new PortalResponseSerializer( null, HttpStatus.NOT_FOUND ).serialize();
        }

        final PortalRequestMapper requestMapper = new PortalRequestMapper( portalRequest );
        final ScriptValue result = this.scriptExports.executeMethod( functionName, requestMapper );

        if ( ( result == null ) || !result.isObject() )
        {
            return null;
        }
        else
        {
            return new PortalResponseSerializer( result ).serialize();
        }
    }
}
