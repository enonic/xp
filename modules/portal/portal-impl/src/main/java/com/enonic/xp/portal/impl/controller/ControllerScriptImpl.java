package com.enonic.xp.portal.impl.controller;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.impl.mapper.WebSocketEventMapper;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketEvent;

final class ControllerScriptImpl
    implements ControllerScript
{
    private static final String ALL_SCRIPT_METHOD_NAME = "all";

    private final ScriptExports scriptExports;

    ControllerScriptImpl( final ScriptExports scriptExports )
    {
        this.scriptExports = scriptExports;
    }

    @Override
    public PortalResponse execute( final PortalRequest portalRequest )
    {
        PortalRequestAccessor.set( portalRequest );

        try
        {
            return Tracer.trace( "controllerScript", () -> doExecute( portalRequest ) );
        }
        finally
        {
            PortalRequestAccessor.remove();
        }
    }

    private void addTraceInfo( final Trace trace )
    {
        trace.put( "script", this.scriptExports.getScript().toString() );
    }

    private PortalResponse doExecute( final PortalRequest portalRequest )
    {
        Tracer.withCurrent( this::addTraceInfo );

        final HttpMethod method = portalRequest.getMethod();
        final boolean isHead = method == HttpMethod.HEAD;
        String runMethod = isHead ? "get" : method.toString().toLowerCase();

        final boolean exists = this.scriptExports.hasMethod( runMethod );
        if ( !exists )
        {
            if ( this.scriptExports.hasMethod( ALL_SCRIPT_METHOD_NAME ) )
            {
                runMethod = ALL_SCRIPT_METHOD_NAME;
            }
            else
            {
                return new PortalResponseSerializer( null, HttpStatus.METHOD_NOT_ALLOWED ).serialize();
            }
        }

        final PortalRequestMapper requestMapper = new PortalRequestMapper( portalRequest );
        final ScriptValue result = this.scriptExports.executeMethod( runMethod, requestMapper );

        return new PortalResponseSerializer( result ).serialize();
    }

    @Override
    public void onSocketEvent( final WebSocketEvent event )
    {
        final boolean exists = this.scriptExports.hasMethod( "webSocketEvent" );
        if ( !exists )
        {
            return;
        }

        this.scriptExports.executeMethod( "webSocketEvent", new WebSocketEventMapper( event ) );
    }
}
