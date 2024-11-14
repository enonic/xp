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
import com.enonic.xp.web.websocket.WebSocketService;

@Component(immediate = true, property = {"applicationKey=admin", "apiKey=event", "displayName=Event API",
    "allowedPrincipals=role:system.admin.login", "mount=true"})
public class EventApiHandler
    implements UniversalApiHandler, EventListener
{
    private static final String GROUP_NAME = "com.enonic.xp.admin.event.api";

    private static final EventJsonSerializer SERIALIZER = new EventJsonSerializer();

    private final WebSocketService webSocketService;

    private final WebSocketManager webSocketManager;

    @Activate
    public EventApiHandler( @Reference final WebSocketService webSocketService, @Reference final WebSocketManager webSocketManager )
    {
        this.webSocketService = webSocketService;
        this.webSocketManager = webSocketManager;
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        final WebResponse.Builder<?> responseBuilder = WebResponse.create();

        if ( !webSocketService.isUpgradeRequest( request.getRawRequest(), null ) )
        {
            responseBuilder.status( HttpStatus.FORBIDDEN );
            return responseBuilder.build();
        }

        final WebSocketConfig webSocketConfig = new WebSocketConfig();
        webSocketConfig.setSubProtocols( List.of( "text" ) );

        responseBuilder.webSocket( webSocketConfig );

        return responseBuilder.build();
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
