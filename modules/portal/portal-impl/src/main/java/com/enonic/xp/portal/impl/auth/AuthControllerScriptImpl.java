package com.enonic.xp.portal.impl.auth;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.auth.AuthControllerScript;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpStatus;

final class AuthControllerScriptImpl
    implements AuthControllerScript
{
    private final ScriptExports scriptExports;

    public AuthControllerScriptImpl( final ScriptExports scriptExports )
    {
        this.scriptExports = scriptExports;
    }

    @Override
    public PortalResponse execute( final String functionName, final PortalRequest portalRequest )
    {
        PortalRequestAccessor.set( portalRequest );
        try
        {
            return doExecute( functionName, portalRequest );
        }
        finally
        {
            PortalRequestAccessor.remove();
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
        return new PortalResponseSerializer( result ).serialize();
    }
}
