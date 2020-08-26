package com.enonic.xp.admin.event.impl;

import java.util.List;

import javax.websocket.Endpoint;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.websocket.EndpointFactory;

@Component
public class EventEndpointFactory
    implements EndpointFactory
{
    private static final List<String> SUB_PROTOCOLS = List.of( "text" );

    private final WebsocketManager webSocketManager;

    @Activate
    public EventEndpointFactory( @Reference final WebsocketManager webSocketManager )
    {
        this.webSocketManager = webSocketManager;
    }

    @Override
    public Endpoint newEndpoint()
    {
        return new EventEndpoint( webSocketManager );
    }

    @Override
    public List<String> getSubProtocols()
    {
        return SUB_PROTOCOLS;
    }
}
