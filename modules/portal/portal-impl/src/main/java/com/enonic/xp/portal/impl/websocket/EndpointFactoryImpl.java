package com.enonic.xp.portal.impl.websocket;

import java.util.List;

import jakarta.websocket.Endpoint;

import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

final class EndpointFactoryImpl
    implements EndpointFactory
{
    WebSocketEndpoint endpoint;

    WebSocketRegistry registry;

    @Override
    public Endpoint newEndpoint()
    {
        return new WebSocketEntryImpl( this.endpoint, this.registry );
    }

    @Override
    public List<String> getSubProtocols()
    {
        return this.endpoint.getConfig().getSubProtocols();
    }
}
