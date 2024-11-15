package com.enonic.xp.admin.event.impl;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.event.impl.json.EventJsonSerializer;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.portal.websocket.WebSocketManager;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

@Component(property = {"applicationKey=admin", "apiKey=event", "displayName=Event API", "allowedPrincipals=role:system.admin.login"})
public class EventApiHandler
    implements UniversalApiHandler, EventListener
{
    private static final String GROUP_NAME = "com.enonic.xp.admin.event";

    private static final EventJsonSerializer SERIALIZER = new EventJsonSerializer();

    private final WebSocketManager webSocketManager;

    @Activate
    public EventApiHandler( @Reference final WebSocketManager webSocketManager )
    {
        this.webSocketManager = webSocketManager;
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        final WebResponse.Builder<?> responseBuilder = WebResponse.create();

        if ( !request.isWebSocket() )
        {
            responseBuilder.status( HttpStatus.BAD_REQUEST );
            return responseBuilder.build();
        }

        responseBuilder.webSocket( createWebSocketConfig() );

        return responseBuilder.build();
    }

    private WebSocketConfig createWebSocketConfig()
    {
        final WebSocketConfig webSocketConfig = new WebSocketConfig();
        webSocketConfig.setSubProtocols( List.of( "text" ) );
        return webSocketConfig;
    }

    @Override
    public void onSocketEvent( final WebSocketEvent event )
    {
        if ( event.getType() == WebSocketEventType.OPEN )
        {
            webSocketManager.addToGroup( GROUP_NAME, event.getSession().getId() );
        }
    }

    @Override
    public void onEvent( final Event event )
    {
        this.webSocketManager.sendToGroup( GROUP_NAME, SERIALIZER.toJson( event ) );
    }
}
