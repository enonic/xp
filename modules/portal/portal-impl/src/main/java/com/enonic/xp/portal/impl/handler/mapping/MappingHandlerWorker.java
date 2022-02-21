package com.enonic.xp.portal.impl.handler.mapping;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

import static com.google.common.base.Strings.nullToEmpty;

final class MappingHandlerWorker
    extends PortalHandlerWorker<PortalRequest>
{
    ResourceService resourceService;

    ControllerScriptFactory controllerScriptFactory;

    ControllerMappingDescriptor mappingDescriptor;

    RendererDelegate rendererDelegate;

    String previewContentSecurityPolicy;

    MappingHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        final ControllerScript controllerScript = getScript();

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", this.request.getContentPath().toString() );
            trace.put( "type", "mapping" );
        }

        final PortalResponse portalResponse = renderController( controllerScript );

        final WebSocketConfig webSocketConfig = portalResponse.getWebSocket();
        final WebSocketContext webSocketContext = this.request.getWebSocketContext();
        if ( webSocketContext != null && webSocketConfig != null )
        {
            final WebSocketEndpoint webSocketEndpoint =
                newWebSocketEndpoint( webSocketConfig, this::getScript, request.getApplicationKey() );
            webSocketContext.apply( webSocketEndpoint );
        }
        return portalResponse;
    }

    private PortalResponse renderController( final ControllerScript controllerScript )
    {
        this.request.setControllerScript( controllerScript );

        final PortalResponse response = rendererDelegate.render( mappingDescriptor, this.request );
        final RenderMode mode = request.getMode();

        if ( mode == RenderMode.LIVE )
        {
            return response;
        }

        final PortalResponse.Builder builder = PortalResponse.create( response );

        if ( mode == RenderMode.INLINE || mode == RenderMode.EDIT )
        {
            builder.header( "X-Frame-Options", "SAMEORIGIN" );
        }

        if ( mode == RenderMode.EDIT )
        {
            builder.removeHeader( "Content-Security-Policy" );
        }
        else if ( !nullToEmpty( previewContentSecurityPolicy ).isBlank() &&
            !response.getHeaders().containsKey( "Content-Security-Policy" ) )
        {
            builder.header( "Content-Security-Policy", previewContentSecurityPolicy );
        }
        return builder.build();
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

    private WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config, final Supplier<ControllerScript> script,
                                                    final ApplicationKey app )
    {
        final Trace trace = Tracer.current();
        if ( trace != null && app != null && !trace.containsKey( "app" ) )
        {
            trace.put( "app", app.toString() );
        }
        return new WebSocketEndpointImpl( config, script );
    }
}
