package com.enonic.xp.portal.impl.controller;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpMethod;

final class ControllerScriptImpl
    implements ControllerScript
{
    private final ScriptExports scriptExports;

    private final PostProcessor postProcessor;

    public ControllerScriptImpl( final ScriptExports scriptExports, final PostProcessor postProcessor )
    {
        this.scriptExports = scriptExports;
        this.postProcessor = postProcessor;
    }

    @Override
    public PortalResponse execute( final PortalRequest portalRequest )
    {
        PortalRequestAccessor.set( portalRequest );

        try
        {
            return this.postProcessor.processResponse( portalRequest, doExecute( portalRequest ) );
        }
        finally
        {
            PortalRequestAccessor.remove();
        }
    }

    private PortalResponse doExecute( final PortalRequest portalRequest )
    {
        final HttpMethod method = portalRequest.getMethod();
        final boolean isHead = method == HttpMethod.HEAD;
        final String runMethod = isHead ? "get" : method.toString().toLowerCase();

        boolean exists = this.scriptExports.hasMethod( runMethod );
        if ( !exists )
        {
            return new PortalResponseSerializer( null ).serialize();
        }

        final PortalRequestMapper requestMapper = new PortalRequestMapper( portalRequest );
        final ScriptValue result = this.scriptExports.executeMethod( runMethod, requestMapper );

        return new PortalResponseSerializer( result ).serialize();
    }

}
