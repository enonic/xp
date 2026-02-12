package com.enonic.xp.portal.impl.controller;

import java.util.Locale;
import java.util.stream.Stream;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.impl.mapper.SseEventMapper;
import com.enonic.xp.portal.impl.mapper.WebSocketEventMapper;
import com.enonic.xp.portal.sse.SseEvent;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketEvent;

final class ControllerScriptImpl
    implements ControllerScript
{
    private final ScriptExports scriptExports;

    ControllerScriptImpl( final ScriptExports scriptExports )
    {
        this.scriptExports = scriptExports;
    }

    @Override
    public PortalResponse execute( final PortalRequest request )
    {
        final ApplicationKey previousApp = request.getApplicationKey();
        request.setApplicationKey( this.scriptExports.getScript().getApplicationKey() );
        PortalRequestAccessor.set( request );
        try
        {
            return Tracer.trace( "controllerScript", trace -> trace.put( "script", this.scriptExports.getScript().toString() ),
                                 () -> doExecute( request ) );
        }
        finally
        {
            PortalRequestAccessor.remove();
            request.setApplicationKey( previousApp );
        }
    }

    private PortalResponse doExecute( final PortalRequest portalRequest )
    {
        final HttpMethod method = portalRequest.getMethod();
        final String methodName = method == HttpMethod.HEAD ? HttpMethod.GET.name() : method.name();

        return Stream.of( methodName, methodName.toLowerCase( Locale.ROOT ), "all" )
            .filter( this.scriptExports::hasMethod )
            .findFirst()
            .map( n -> new PortalResponseSerializer( this.scriptExports.executeMethod( n, new PortalRequestMapper( portalRequest ) ) ) )
            .orElseGet( () -> new PortalResponseSerializer( null, HttpStatus.METHOD_NOT_ALLOWED ) )
            .serialize();
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

    @Override
    public void onSseEvent( final SseEvent event )
    {
        final boolean exists = this.scriptExports.hasMethod( "sseEvent" );
        if ( !exists )
        {
            return;
        }

        this.scriptExports.executeMethod( "sseEvent", new SseEventMapper( event ) );
    }
}
