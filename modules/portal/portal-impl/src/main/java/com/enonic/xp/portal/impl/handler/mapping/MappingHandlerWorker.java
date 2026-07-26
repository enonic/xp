package com.enonic.xp.portal.impl.handler.mapping;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.impl.sse.SseEndpointImpl;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.sse.SseConfig;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

final class MappingHandlerWorker
{
    private final PortalRequest request;

    ResourceService resourceService;

    ControllerScriptFactory controllerScriptFactory;

    ControllerMappingDescriptor mappingDescriptor;

    RendererDelegate rendererDelegate;

    SseManager sseManager;

    MappingHandlerWorker( final PortalRequest request )
    {
        this.request = request;
    }

    public PortalResponse execute()
        throws Exception
    {
        final ControllerScript controllerScript = getScript();

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", this.request.getContentPath() != null ? this.request.getContentPath().toString() : null );
            trace.put( "type", "mapping" );
        }

        this.request.setControllerScript( controllerScript );

        // render bound to one script context: the controller executes there, and a connection
        // opened by this request dispatches its events to that exact context
        final ControllerScript[] boundRef = new ControllerScript[1];
        final PortalResponse portalResponse = controllerScript.executeBound( bound -> {
            boundRef[0] = bound;
            return rendererDelegate.render( mappingDescriptor, this.request );
        } );
        final ControllerScript boundScript = boundRef[0];

        final WebSocketConfig webSocketConfig = portalResponse.getWebSocket();
        final WebSocketContext webSocketContext = this.request.getWebSocketContext();
        if ( webSocketContext != null && webSocketConfig != null )
        {
            final WebSocketEndpoint webSocketEndpoint =
                newWebSocketEndpoint( webSocketConfig, boundScript, mappingDescriptor.getController().getApplicationKey() );
            webSocketContext.apply( webSocketEndpoint );
        }

        final SseConfig sseConfig = portalResponse.getSse();
        if ( sseConfig != null && this.sseManager != null )
        {
            final SseEndpointImpl sseEndpoint =
                new SseEndpointImpl( sseConfig, boundScript, mappingDescriptor.getController().getApplicationKey() );
            this.sseManager.setupSse( this.request, sseEndpoint );
        }

        return portalResponse;
    }

    private ControllerScript getScript()
    {
        final Resource resource = this.resourceService.getResource( mappingDescriptor.getController() );
        if ( !resource.exists() )
        {
            throw WebException.notFound( String.format( "Controller [%s] not found", mappingDescriptor.getController() ) );
        }
        return this.controllerScriptFactory.fromScript( resource.getKey() );
    }

    private WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config, final ControllerScript script,
                                                    final ApplicationKey app )
    {
        final Trace trace = Tracer.current();
        if ( trace != null && app != null && !trace.containsKey( "app" ) )
        {
            trace.put( "app", app.toString() );
        }
        return new WebSocketEndpointImpl( config, script, app );
    }
}
